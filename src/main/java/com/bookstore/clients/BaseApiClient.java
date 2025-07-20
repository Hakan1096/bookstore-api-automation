package com.bookstore.clients;

import com.bookstore.config.ApiConfig;
import com.bookstore.models.ApiResponse;
import com.bookstore.utils.JsonUtils;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class BaseApiClient {
  protected final ApiConfig config;

  public BaseApiClient() {
    this.config = ApiConfig.getInstance();
    configureRestAssured();
  }

  private void configureRestAssured() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    if (config.isLogRequests()) {
      RestAssured.filters(new AllureRestAssured());
    }
  }

  protected RequestSpecification getBaseRequestSpec() {
    return RestAssured.given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON);
  }
  protected <T> ApiResponse<T> executeGet(String endpoint, Class<T> responseType) {
    return executeRequest(() -> getBaseRequestSpec().get(endpoint), responseType);
  }

  protected <T> ApiResponse<T> executePost(String endpoint, Object requestBody, Class<T> responseType) {
    return executeRequest(() -> getBaseRequestSpec().body(requestBody).post(endpoint), responseType);
  }

  protected <T> ApiResponse<T> executePut(String endpoint, Object requestBody, Class<T> responseType) {
    return executeRequest(() -> getBaseRequestSpec().body(requestBody).put(endpoint), responseType);
  }

  protected <T> ApiResponse<T> executeDelete(String endpoint, Class<T> responseType) {
    return executeRequest(() -> getBaseRequestSpec().delete(endpoint), responseType);
  }

  protected <T> ApiResponse<T> executePatch(String endpoint, Object requestBody, Class<T> responseType) {
    return executeRequest(() -> getBaseRequestSpec().body(requestBody).patch(endpoint), responseType);
  }

  private <T> ApiResponse<T> executeRequest(RequestExecutor executor, Class<T> responseType) {
    long startTime = System.currentTimeMillis();

    try {
      Response response = executor.execute();
      long responseTime = System.currentTimeMillis() - startTime;

      logResponse(response, responseTime);

      return buildApiResponse(response, responseType, responseTime);

    } catch (Exception e) {
      log.error("Request execution failed: {}", e.getMessage(), e);
      throw new RuntimeException("API request failed", e);
    }
  }

  private <T> ApiResponse<T> buildApiResponse(Response response, Class<T> responseType, long responseTime) {
    ApiResponse.ApiResponseBuilder<T> builder = ApiResponse.<T>builder()
      .statusCode(response.getStatusCode())
      .statusMessage(response.getStatusLine())
      .responseTime(responseTime)
      .rawResponse(response.asString())
      .headers(convertHeaders(response.getHeaders().asList()));

    // Parse response body if present and successful
    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
      try {
        if (responseType != Void.class && !response.asString().isEmpty()) {
          T data = JsonUtils.fromJson(response.asString(), responseType);
          builder.data(data);
        }
      } catch (Exception e) {
        log.warn("Failed to parse response body: {}", e.getMessage());
      }
    }

    return builder.build();
  }

  private Map<String, String> convertHeaders(java.util.List<io.restassured.http.Header> headers) {
    Map<String, String> headerMap = new HashMap<>();
    headers.forEach(header -> headerMap.put(header.getName(), header.getValue()));
    return headerMap;
  }

  private void logResponse(Response response, long responseTime) {
    if (config.isLogResponses()) {
      log.info("Response - Status: {}, Time: {}ms, Size: {} bytes",
        response.getStatusCode(), responseTime, response.asString().length());

      if (log.isDebugEnabled()) {
        log.debug("Response Body: {}", response.asString());
      }
    }
  }

  // Retry mechanism for failed requests
  protected <T> ApiResponse<T> executeWithRetry(RequestExecutor executor, Class<T> responseType) {
    int attempts = 0;
    Exception lastException = null;

    while (attempts < config.getRetryCount()) {
      try {
        return executeRequest(executor, responseType);
      } catch (Exception e) {
        lastException = e;
        attempts++;
        log.warn("Request attempt {} failed: {}", attempts, e.getMessage());

        if (attempts < config.getRetryCount()) {
          try {
            Thread.sleep(1000 * attempts); // Exponential backoff
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during retry", ie);
          }
        }
      }
    }

    throw new RuntimeException("Request failed after " + config.getRetryCount() + " attempts", lastException);
  }

  @FunctionalInterface
  protected interface RequestExecutor {
    Response execute();
  }
}
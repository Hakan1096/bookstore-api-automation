package com.bookstore.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ApiResponse<T> {
  private int statusCode;
  private String statusMessage;
  private T data;
  private List<String> errors;
  private Map<String, String> headers;
  private long responseTime;
  private String rawResponse;

  public boolean isSuccess() {
    return statusCode >= 200 && statusCode < 300;
  }

  public boolean isClientError() {
    return statusCode >= 400 && statusCode < 500;
  }

  public boolean isServerError() {
    return statusCode >= 500;
  }

  public boolean hasErrors() {
    return errors != null && !errors.isEmpty();
  }
}

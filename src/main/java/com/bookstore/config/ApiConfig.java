package com.bookstore.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
@Slf4j
public class ApiConfig {
  private static final String CONFIG_FILE = "config.properties";
  private static ApiConfig instance;

  private String baseUrl;
  private int timeout;
  private int retryCount;
  private boolean logRequests;
  private boolean logResponses;
  private int parallelThreads;
  private boolean dataCleanup;
  private boolean generateReport;

  private ApiConfig() {
    loadConfiguration();
  }

  public static synchronized ApiConfig getInstance() {
    if (instance == null) {
      instance = new ApiConfig();
    }
    return instance;
  }

  private void loadConfiguration() {
    Properties properties = new Properties();

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (inputStream != null) {
        properties.load(inputStream);
        log.info("Loaded configuration from {}", CONFIG_FILE);
      } else {
        log.warn("Configuration file {} not found, using defaults", CONFIG_FILE);
      }
    } catch (IOException e) {
      log.error("Error loading configuration file: {}", e.getMessage());
    }

    // Load from properties with fallback to environment variables and defaults
    this.baseUrl = getProperty(properties, "api.base.url", "API_BASE_URL",
      "https://fakerestapi.azurewebsites.net");
    this.timeout = Integer.parseInt(getProperty(properties, "api.timeout", "API_TIMEOUT", "30000"));
    this.retryCount = Integer.parseInt(getProperty(properties, "api.retry.count", "API_RETRY_COUNT", "3"));
    this.logRequests = Boolean.parseBoolean(getProperty(properties, "api.log.requests", "LOG_REQUESTS", "true"));
    this.logResponses = Boolean.parseBoolean(getProperty(properties, "api.log.responses", "LOG_RESPONSES", "true"));
    this.parallelThreads = Integer.parseInt(getProperty(properties, "test.parallel.threads", "PARALLEL_THREADS", "5"));
    this.dataCleanup = Boolean.parseBoolean(getProperty(properties, "test.data.cleanup", "DATA_CLEANUP", "true"));
    this.generateReport = Boolean.parseBoolean(getProperty(properties, "report.generate.after.test",
      "GENERATE_REPORT", "true"));

    log.info("Configuration loaded - Base URL: {}, Timeout: {}ms, Parallel Threads: {}",
      baseUrl, timeout, parallelThreads);
  }

  private String getProperty(Properties properties, String propertyKey, String envKey, String defaultValue) {
    // Priority: System Property > Environment Variable > Properties File > Default
    String value = System.getProperty(propertyKey);
    if (StringUtils.isBlank(value)) {
      value = System.getenv(envKey);
    }
    if (StringUtils.isBlank(value)) {
      value = properties.getProperty(propertyKey);
    }
    if (StringUtils.isBlank(value)) {
      value = defaultValue;
    }
    return value;
  }

  public String getBooksEndpoint() {
    return baseUrl + "/api/v1/Books";
  }

  public String getAuthorsEndpoint() {
    return baseUrl + "/api/v1/Authors";
  }

  public String getBookByIdEndpoint(int id) {
    return getBooksEndpoint() + "/" + id;
  }

  public String getAuthorByIdEndpoint(int id) {
    return getAuthorsEndpoint() + "/" + id;
  }
}
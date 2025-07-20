package com.bookstore.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class TestConfig {
  private static TestConfig instance;

  private int maxRetryAttempts;
  private long retryDelayMs;
  private boolean softAssertions;
  private boolean captureScreenshots;
  private String testDataPath;
  private String reportPath;

  private TestConfig() {
    initializeDefaults();
  }

  public static synchronized TestConfig getInstance() {
    if (instance == null) {
      instance = new TestConfig();
    }
    return instance;
  }

  private void initializeDefaults() {
    this.maxRetryAttempts = 3;
    this.retryDelayMs = 1000;
    this.softAssertions = false;
    this.captureScreenshots = true;
    this.testDataPath = "src/main/resources/test-data/";
    this.reportPath = "target/allure-results/";

    log.info("Test configuration initialized with defaults");
  }
}
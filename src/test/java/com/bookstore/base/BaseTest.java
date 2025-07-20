package com.bookstore.base;

import com.bookstore.clients.AuthorApiClient;
import com.bookstore.clients.BookApiClient;
import com.bookstore.config.ApiConfig;
import com.bookstore.config.TestConfig;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@Slf4j
@Epic("Bookstore API Automation")
public abstract class BaseTest {
  protected BookApiClient bookApiClient;
  protected AuthorApiClient authorApiClient;
  protected ApiConfig apiConfig;
  protected TestConfig testConfig;
  protected TestDataGenerator testDataGenerator;

  @BeforeSuite(alwaysRun = true)
  public void beforeSuite() {
    log.info("=== Starting Test Suite Execution ===");
    apiConfig = ApiConfig.getInstance();
    testConfig = TestConfig.getInstance();

    log.info("API Base URL: {}", apiConfig.getBaseUrl());
    log.info("Parallel Threads: {}", apiConfig.getParallelThreads());

    // Verify API connectivity
    verifyApiConnectivity();
  }

  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
    log.info("=== Starting Test Class: {} ===", this.getClass().getSimpleName());
    initializeClients();
  }

  @BeforeMethod(alwaysRun = true)
  public void beforeMethod(Method method) {
    log.info("Starting test: {}", method.getName());
    testDataGenerator = new TestDataGenerator();
  }

  @AfterMethod(alwaysRun = true)
  public void afterMethod(ITestResult result) {
    String testName = result.getMethod().getMethodName();

    if (result.getStatus() == ITestResult.SUCCESS) {
      log.info("✅ Test PASSED: {}", testName);
    } else if (result.getStatus() == ITestResult.FAILURE) {
      log.error("❌ Test FAILED: {} - {}", testName, result.getThrowable().getMessage());
    } else if (result.getStatus() == ITestResult.SKIP) {
      log.warn("⏭️ Test SKIPPED: {}", testName);
    }

    // Cleanup test data if enabled
    if (testConfig.isDataCleanup()) {
      performDataCleanup();
    }
  }

  @AfterClass(alwaysRun = true)
  public void afterClass() {
    log.info("=== Completed Test Class: {} ===", this.getClass().getSimpleName());
  }

  @AfterSuite(alwaysRun = true)
  public void afterSuite() {
    log.info("=== Test Suite Execution Completed ===");
  }

  private void initializeClients() {
    bookApiClient = new BookApiClient();
    authorApiClient = new AuthorApiClient();
    log.info("API clients initialized successfully");
  }

  private void verifyApiConnectivity() {
    try {
      BookApiClient tempClient = new BookApiClient();
      tempClient.getAllBooks();
      log.info("✅ API connectivity verified");
    } catch (Exception e) {
      log.error("❌ API connectivity check failed: {}", e.getMessage());
      throw new RuntimeException("Unable to connect to API", e);
    }
  }

  private void performDataCleanup() {
    // Override in subclasses if specific cleanup is needed
    log.debug("Performing data cleanup...");
  }

  // Utility methods for tests
  protected void waitForSeconds(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Wait interrupted: {}", e.getMessage());
    }
  }

  protected void logTestStep(String step) {
    log.info("🔹 {}", step);
  }
}

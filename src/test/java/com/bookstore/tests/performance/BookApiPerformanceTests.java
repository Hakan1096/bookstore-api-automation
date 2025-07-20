package com.bookstore.tests.performance;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Book;
import com.bookstore.utils.PerformanceUtils;
import com.bookstore.utils.TestDataGenerator;
import com.bookstore.utils.TestGroupConstants;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Books API - Performance Tests")
public class BookApiPerformanceTests extends BaseTest {

  @Test(description = "Verify Books API can handle concurrent GET requests",
    groups = {TestGroupConstants.PERFORMANCE, TestGroupConstants.BOOKS})
  @Description("Load test for GET /api/v1/Books endpoint with multiple concurrent users")
  @Severity(SeverityLevel.NORMAL)
  @Story("Concurrent Load Testing")
  public void testConcurrentGetAllBooks() {
    logTestStep("Starting concurrent load test for GET all books");

    int numberOfRequests = 50;
    int concurrency = 10;

    PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
      () -> {
        ApiResponse<List<Book>> response = bookApiClient.getAllBooks();
        return response.isSuccess();
      },
      numberOfRequests,
      concurrency
    );

    // Performance assertions
    Assert.assertTrue(result.getSuccessRate() >= 95.0,
      "Success rate should be at least 95%, actual: " + result.getSuccessRate() + "%");
    Assert.assertTrue(result.getAverageResponseTime() < 2000,
      "Average response time should be under 2 seconds, actual: " + result.getAverageResponseTime() + "ms");
    Assert.assertTrue(result.getThroughput() > 5,
      "Throughput should be at least 5 requests/second, actual: " + result.getThroughput());

    logTestStep("Concurrent load test completed successfully");
  }

  @Test(description = "Verify Books API response time consistency",
    groups = {TestGroupConstants.PERFORMANCE, TestGroupConstants.BOOKS})
  @Description("Test response time consistency over multiple requests")
  @Severity(SeverityLevel.NORMAL)
  @Story("Response Time Analysis")
  public void testResponseTimeConsistency() {
    logTestStep("Testing response time consistency for book operations");

    int iterations = 20;

    PerformanceUtils.PerformanceResult result = PerformanceUtils.measureResponseTime(
      () -> {
        ApiResponse<List<Book>> response = bookApiClient.getAllBooks();
        return response.isSuccess() && response.getResponseTime() < 5000;
      },
      iterations
    );

    // Consistency assertions
    Assert.assertTrue(result.getSuccessRate() == 100.0,
      "All requests should succeed, actual success rate: " + result.getSuccessRate() + "%");

    long responseTimeVariance = result.getMaxResponseTime() - result.getMinResponseTime();
    Assert.assertTrue(responseTimeVariance < 3000,
      "Response time variance should be under 3 seconds, actual: " + responseTimeVariance + "ms");

    logTestStep("Response time consistency test completed");
  }

  @Test(description = "Verify Books API can handle burst requests",
    groups = {TestGroupConstants.PERFORMANCE, TestGroupConstants.BOOKS})
  @Description("Test API behavior under burst traffic conditions")
  @Severity(SeverityLevel.NORMAL)
  @Story("Burst Load Testing")
  public void testBurstLoad() {
    logTestStep("Testing burst load handling for book creation");

    PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
      () -> {
        Book newBook = TestDataGenerator.generateValidBook();
        ApiResponse<Book> response = bookApiClient.createBook(newBook);
        return response.isSuccess();
      },
      20, // Burst of 20 requests
      20  // All at once
    );

    // Burst load assertions
    Assert.assertTrue(result.getSuccessRate() >= 80.0,
      "Success rate under burst should be at least 80%, actual: " + result.getSuccessRate() + "%");
    Assert.assertTrue(result.getFailedRequests() <= 4,
      "Failed requests should be minimal under burst, actual: " + result.getFailedRequests());

    logTestStep("Burst load test completed");
  }

  @Test(description = "Verify Books API performance under sustained load",
    groups = {TestGroupConstants.PERFORMANCE, TestGroupConstants.BOOKS})
  @Description("Test API performance under sustained load over time")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Sustained Load Testing")
  public void testSustainedLoad() {
    logTestStep("Testing sustained load for book operations");

    // Simulate sustained load with mixed operations
    PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
      () -> {
        // Mix of different operations
        int operation = (int) (Math.random() * 3);
        ApiResponse<?> response;

        switch (operation) {
        case 0: // GET all books
          response = bookApiClient.getAllBooks();
          break;
        case 1: // GET book by ID
          response = bookApiClient.getBookById(1);
          break;
        case 2: // CREATE book
          Book newBook = TestDataGenerator.generateValidBook();
          response = bookApiClient.createBook(newBook);
          break;
        default:
          response = bookApiClient.getAllBooks();
        }

        return response.isSuccess();
      },
      100, // 100 requests
      5    // 5 concurrent users
    );

    // Sustained load assertions
    Assert.assertTrue(result.getSuccessRate() >= 90.0,
      "Success rate under sustained load should be at least 90%, actual: " + result.getSuccessRate() + "%");
    Assert.assertTrue(result.getAverageResponseTime() < 3000,
      "Average response time should remain under 3 seconds, actual: " + result.getAverageResponseTime() + "ms");

    logTestStep("Sustained load test completed successfully");
  }
}

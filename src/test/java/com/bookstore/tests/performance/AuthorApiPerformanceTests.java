package com.bookstore.tests.performance;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.utils.PerformanceUtils;
import com.bookstore.utils.TestDataGenerator;
import com.bookstore.utils.TestGroupConstants;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Authors API - Performance Tests")
public class AuthorApiPerformanceTests extends BaseTest {

  @Test(description = "Verify Authors API concurrent access performance",
    groups = {TestGroupConstants.PERFORMANCE, TestGroupConstants.AUTHORS})
  @Description("Load test for Authors API with concurrent users")
  @Severity(SeverityLevel.NORMAL)
  @Story("Concurrent Performance")
  public void testConcurrentAuthorOperations() {
    logTestStep("Starting concurrent performance test for authors API");

    PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
      () -> {
        // Alternate between GET all authors and GET author by ID
        if (Math.random() > 0.5) {
          ApiResponse<List<Author>> response = authorApiClient.getAllAuthors();
          return response.isSuccess();
        } else {
          ApiResponse<Author> response = authorApiClient.getAuthorById(1);
          return response.isSuccess() || response.getStatusCode() == 404; // 404 is acceptable
        }
      },
      30,
      6
    );

    Assert.assertTrue(result.getSuccessRate() >= 90.0,
      "Authors API success rate should be at least 90%");
    Assert.assertTrue(result.getAverageResponseTime() < 2500,
      "Authors API average response time should be under 2.5 seconds");

    logTestStep("Authors API concurrent performance test completed");
  }
}

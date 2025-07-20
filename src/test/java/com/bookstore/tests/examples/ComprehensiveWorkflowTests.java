package com.bookstore.tests.examples;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.bookstore.utils.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Feature("Comprehensive Workflow Examples")
public class ComprehensiveWorkflowTests extends BaseTest {

  @Test(description = "Demonstrate complete framework capabilities",
    groups = {TestGroupConstants.INTEGRATION, TestGroupConstants.REGRESSION})
  @Description("Comprehensive test showcasing all framework features and utilities")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Framework Showcase")
  public void testCompleteFrameworkShowcase() {
    logTestStep("Starting comprehensive framework showcase test");

    // 1. Load test data from files
    Book bookFromFile = FileUtils.getValidBook();
    Author authorFromFile = FileUtils.getValidAuthor();

    // 2. Generate dynamic test data
    Book generatedBook = TestDataGenerator.generateValidBook();
    Author generatedAuthor = TestDataGenerator.generateValidAuthor();

    // 3. Create book with retry mechanism
    Book createdBook = RetryUtils.retry(() -> {
      ApiResponse<Book> response = bookApiClient.createBook(bookFromFile);
      if (response.isSuccess()) {
        return response.getData();
      }
      throw new RuntimeException("Failed to create book");
    }, 3, 1000);

    Assert.assertNotNull(createdBook.getId(), "Book should be created with ID");

    // 4. Wait for book to be available
    WaitUtils.waitFor(
      Duration.ofSeconds(10),
      Duration.ofSeconds(1),
      () -> bookApiClient.getBookById(createdBook.getId()).isSuccess(),
      "Book should be available after creation"
    );

    // 5. Create author associated with the book
    authorFromFile.setIdBook(createdBook.getId());
    ApiResponse<Author> authorResponse = authorApiClient.createAuthor(authorFromFile);
    Assert.assertTrue(authorResponse.isSuccess(), "Author creation should succeed");

    // 6. Perform validation using custom matcher
    ApiResponse<Book> getBookResponse = bookApiClient.getBookById(createdBook.getId());
    ResponseValidator.validateBookResponse(getBookResponse);

    // 7. Test performance for this specific workflow
    PerformanceUtils.PerformanceResult performanceResult = PerformanceUtils.measureResponseTime(
      () -> {
        ApiResponse<Book> response = bookApiClient.getBookById(createdBook.getId());
        return response.isSuccess();
      },
      10
    );

    Assert.assertTrue(performanceResult.getAverageResponseTime() < 2000,
      "Individual book retrieval should be fast");

    // 8. Verify data consistency
    List<Author> authorsForBook = authorApiClient.getAuthorsByBookId(createdBook.getId());
    Assert.assertTrue(authorsForBook.size() >= 1, "Book should have at least one author");

    // 9. Update operations with validation
    createdBook.setTitle("Updated " + createdBook.getTitle());
    ApiResponse<Book> updateResponse = bookApiClient.updateBook(createdBook.getId(), createdBook);
    Assert.assertTrue(updateResponse.isSuccess(), "Book update should succeed");

    // 10. Cleanup (if enabled)
    if (testConfig.isDataCleanup()) {
      bookApiClient.deleteBook(createdBook.getId());
      authorApiClient.deleteAuthor(authorResponse.getData().getId());
    }

    logTestStep("Comprehensive framework showcase completed successfully");
  }

  @Test(description = "Demonstrate bulk operations and data handling",
    groups = {TestGroupConstants.INTEGRATION})
  @Description("Test bulk operations using file-based test data")
  @Severity(SeverityLevel.NORMAL)
  @Story("Bulk Operations")
  public void testBulkOperationsExample() {
    logTestStep("Starting bulk operations demonstration");

    // Load bulk test data from files
    List<Book> bulkBooks = FileUtils.getBulkBooks();
    List<Author> bulkAuthors = FileUtils.getBulkAuthors();

    Assert.assertFalse(bulkBooks.isEmpty(), "Bulk books data should be loaded");
    Assert.assertFalse(bulkAuthors.isEmpty(), "Bulk authors data should be loaded");

    // Create books in parallel
    bulkBooks.parallelStream().forEach(book -> {
      try {
        ApiResponse<Book> response = bookApiClient.createBook(book);
        if (response.isSuccess()) {
          logTestStep("Created book: " + response.getData().getTitle());
        }
      } catch (Exception e) {
        log.warn("Failed to create book: {}", e.getMessage());
      }
    });

    // Verify bulk creation didn't significantly impact performance
    PerformanceUtils.PerformanceResult getPerformance = PerformanceUtils.measureResponseTime(
      () -> {
        ApiResponse<List<Book>> response = bookApiClient.getAllBooks();
        return response.isSuccess();
      },
      5
    );

    Assert.assertTrue(getPerformance.getAverageResponseTime() < 3000,
      "API should maintain performance after bulk operations");

    logTestStep("Bulk operations demonstration completed");
  }

  @Test(description = "Demonstrate error handling and recovery",
    groups = {TestGroupConstants.NEGATIVE})
  @Description("Test framework error handling capabilities")
  @Severity(SeverityLevel.NORMAL)
  @Story("Error Handling")
  public void testErrorHandlingExample() {
    logTestStep("Starting error handling demonstration");

    // Test with invalid data
    Book invalidBook = FileUtils.getInvalidBook();

    // Attempt operation with retry on specific condition
    try {
      RetryUtils.retry(() -> {
        ApiResponse<Book> response = bookApiClient.createBook(invalidBook);
        if (response.isSuccess()) {
          return response.getData();
        }
        // This should consistently fail, so we'll eventually exhaust retries
        throw new RuntimeException("Invalid book data");
      }, 3, 500);

      Assert.fail("Should not succeed with invalid book data");
    } catch (RuntimeException e) {
      // Expected - invalid data should fail
      logTestStep("Correctly handled invalid data with retries");
    }

    // Test handling of non-existent resources
    int nonExistentId = TestDataGenerator.generateNonExistentId();
    ApiResponse<Book> notFoundResponse = bookApiClient.getBookById(nonExistentId);

    Assert.assertFalse(notFoundResponse.isSuccess(), "Non-existent book should return error");
    Assert.assertEquals(notFoundResponse.getStatusCode(), 404, "Should return 404 for non-existent book");

    logTestStep("Error handling demonstration completed");
  }
}

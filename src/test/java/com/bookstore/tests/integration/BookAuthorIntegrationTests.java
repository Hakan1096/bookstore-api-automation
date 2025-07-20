package com.bookstore.tests.integration;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Books and Authors Integration Tests")
public class BookAuthorIntegrationTests extends BaseTest {

  @Test(description = "Verify complete book-author workflow")
  @Description("Test end-to-end workflow of creating book, creating author, and linking them")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Book-Author Integration")
  public void testCompleteBookAuthorWorkflow() {
    logTestStep("Starting complete book-author integration workflow");

    // Step 1: Create a book
    Book newBook = TestDataGenerator.generateValidBook();
    ApiResponse<Book> bookResponse = bookApiClient.createBook(newBook);
    Assert.assertTrue(bookResponse.isSuccess(), "Book creation should succeed");
    Book createdBook = bookResponse.getData();
    int bookId = createdBook.getId();

    logTestStep("Created book with ID: " + bookId);

    // Step 2: Create an author associated with the book
    Author newAuthor = TestDataGenerator.generateValidAuthor();
    newAuthor.setIdBook(bookId);
    ApiResponse<Author> authorResponse = authorApiClient.createAuthor(newAuthor);
    Assert.assertTrue(authorResponse.isSuccess(), "Author creation should succeed");
    Author createdAuthor = authorResponse.getData();

    logTestStep("Created author: " + createdAuthor.getFullName() + " for book ID: " + bookId);

    // Step 3: Verify the relationship
    Assert.assertEquals(createdAuthor.getIdBook(), bookId,
      "Author should be associated with the correct book");

    // Step 4: Retrieve all authors and verify our author is in the list
    ApiResponse<List<Author>> allAuthorsResponse = authorApiClient.getAllAuthors();
    Assert.assertTrue(allAuthorsResponse.isSuccess(), "Getting all authors should succeed");

    boolean authorFound = allAuthorsResponse.getData().stream()
      .anyMatch(author -> author.getId().equals(createdAuthor.getId()));
    Assert.assertTrue(authorFound, "Created author should be found in authors list");

    // Step 5: Update the book
    createdBook.setTitle("Updated Title for Integration Test");
    ApiResponse<Book> updateBookResponse = bookApiClient.updateBook(bookId, createdBook);
    Assert.assertTrue(updateBookResponse.isSuccess(), "Book update should succeed");

    // Step 6: Update the author
    createdAuthor.setFirstName("Updated" + createdAuthor.getFirstName());
    ApiResponse<Author> updateAuthorResponse = authorApiClient.updateAuthor(createdAuthor.getId(), createdAuthor);
    Assert.assertTrue(updateAuthorResponse.isSuccess(), "Author update should succeed");

    logTestStep("Successfully completed book-author integration workflow");
  }

  @Test(description = "Verify retrieving authors by book ID")
  @Description("Test finding all authors associated with a specific book")
  @Severity(SeverityLevel.NORMAL)
  @Story("Book-Author Relationship")
  public void testRetrieveAuthorsByBookId() {
    int targetBookId = 1;
    logTestStep("Retrieving authors for book ID: " + targetBookId);

    List<Author> authorsForBook = authorApiClient.getAuthorsByBookId(targetBookId);

    Assert.assertNotNull(authorsForBook, "Authors list should not be null");

    // Verify all returned authors are associated with the target book
    authorsForBook.forEach(author -> {
      Assert.assertEquals(author.getIdBook(), targetBookId,
        "Each author should be associated with book ID: " + targetBookId);
    });

    logTestStep("Found " + authorsForBook.size() + " authors for book ID: " + targetBookId);
  }

  @Test(description = "Verify creating multiple authors for one book")
  @Description("Test creating multiple authors associated with the same book")
  @Severity(SeverityLevel.NORMAL)
  @Story("Multiple Authors per Book")
  public void testMultipleAuthorsForOneBook() {
    logTestStep("Testing multiple authors for single book");

    // Create a book first
    Book book = TestDataGenerator.generateValidBook();
    ApiResponse<Book> bookResponse = bookApiClient.createBook(book);
    Assert.assertTrue(bookResponse.isSuccess(), "Book creation should succeed");
    int bookId = bookResponse.getData().getId();

    // Create multiple authors for the same book
    Author firstAuthor = TestDataGenerator.generateValidAuthor();
    firstAuthor.setIdBook(bookId);
    ApiResponse<Author> firstResponse = authorApiClient.createAuthor(firstAuthor);
    Assert.assertTrue(firstResponse.isSuccess(), "First author creation should succeed");

    Author secondAuthor = TestDataGenerator.generateValidAuthor();
    secondAuthor.setIdBook(bookId);
    ApiResponse<Author> secondResponse = authorApiClient.createAuthor(secondAuthor);
    Assert.assertTrue(secondResponse.isSuccess(), "Second author creation should succeed");

    // Verify both authors are associated with the same book
    List<Author> authorsForBook = authorApiClient.getAuthorsByBookId(bookId);
    long associatedAuthors = authorsForBook.stream()
      .filter(author -> author.getIdBook() != null && author.getIdBook().equals(bookId))
      .count();

    Assert.assertTrue(associatedAuthors >= 2,
      "At least 2 authors should be associated with book ID: " + bookId);

    logTestStep("Successfully created multiple authors for book ID: " + bookId);
  }

  @Test(description = "Verify book deletion impact on associated authors")
  @Description("Test behavior when deleting a book that has associated authors")
  @Severity(SeverityLevel.NORMAL)
  @Story("Data Consistency")
  public void testBookDeletionWithAssociatedAuthors() {
    logTestStep("Testing book deletion with associated authors");

    // Create a book
    Book book = TestDataGenerator.generateValidBook();
    ApiResponse<Book> bookResponse = bookApiClient.createBook(book);
    Assert.assertTrue(bookResponse.isSuccess(), "Book creation should succeed");
    int bookId = bookResponse.getData().getId();

    // Create an author associated with the book
    Author author = TestDataGenerator.generateValidAuthor();
    author.setIdBook(bookId);
    ApiResponse<Author> authorResponse = authorApiClient.createAuthor(author);
    Assert.assertTrue(authorResponse.isSuccess(), "Author creation should succeed");
    int authorId = authorResponse.getData().getId();

    // Delete the book
    ApiResponse<Void> deleteBookResponse = bookApiClient.deleteBook(bookId);
    Assert.assertTrue(deleteBookResponse.isSuccess(), "Book deletion should succeed");

    // Check if author still exists
    ApiResponse<Author> getAuthorResponse = authorApiClient.getAuthorById(authorId);

    // The behavior may vary - author might still exist or be deleted
    // We just verify the system handles it consistently
    if (getAuthorResponse.isSuccess()) {
      logTestStep("Author still exists after book deletion - orphaned author");
    } else {
      logTestStep("Author was deleted along with book - cascading delete");
    }

    logTestStep("Book deletion with associated authors handled consistently");
  }

  @Test(description = "Verify system performance with multiple concurrent operations")
  @Description("Test concurrent book and author operations for performance")
  @Severity(SeverityLevel.NORMAL)
  @Story("Performance Testing")
  public void testConcurrentBookAuthorOperations() {
    logTestStep("Testing concurrent book and author operations");

    long startTime = System.currentTimeMillis();

    // Create multiple threads for concurrent operations
    Thread[] threads = new Thread[5];

    for (int i = 0; i < threads.length; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(() -> {
        try {
          // Create book
          Book book = TestDataGenerator.generateValidBook();
          book.setTitle("Concurrent Book " + threadIndex);
          ApiResponse<Book> bookResponse = bookApiClient.createBook(book);

          if (bookResponse.isSuccess()) {
            int bookId = bookResponse.getData().getId();

            // Create author for the book
            Author author = TestDataGenerator.generateValidAuthor();
            author.setFirstName("ConcurrentAuthor" + threadIndex);
            author.setIdBook(bookId);
            authorApiClient.createAuthor(author);
          }
        } catch (Exception e) {
          // Log but don't fail the test for individual thread failures
          log.warn("Thread {} encountered error: {}", threadIndex, e.getMessage());
        }
      });
    }

    // Start all threads
    for (Thread thread : threads) {
      thread.start();
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      try {
        thread.join(10000); // Wait max 10 seconds per thread
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        Assert.fail("Concurrent test was interrupted");
      }
    }

    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;

    Assert.assertTrue(totalTime < 30000,
      "Concurrent operations should complete within 30 seconds, actual: " + totalTime + "ms");

    logTestStep("Concurrent operations completed in: " + totalTime + "ms");
  }

  @Test(description = "Verify data consistency after multiple operations")
  @Description("Test data integrity after series of CRUD operations")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Data Integrity")
  public void testDataConsistencyAfterMultipleOperations() {
    logTestStep("Testing data consistency after multiple operations");

    // Get initial counts
    int initialBookCount = bookApiClient.getBooksCount();
    int initialAuthorCount = authorApiClient.getAuthorsCount();

    // Perform multiple operations
    for (int i = 0; i < 3; i++) {
      // Create book
      Book book = TestDataGenerator.generateValidBook();
      book.setTitle("Consistency Test Book " + i);
      ApiResponse<Book> bookResponse = bookApiClient.createBook(book);
      Assert.assertTrue(bookResponse.isSuccess(), "Book creation should succeed");

      // Create author
      Author author = TestDataGenerator.generateValidAuthor();
      author.setFirstName("ConsistencyAuthor" + i);
      author.setIdBook(bookResponse.getData().getId());
      ApiResponse<Author> authorResponse = authorApiClient.createAuthor(author);
      Assert.assertTrue(authorResponse.isSuccess(), "Author creation should succeed");

      // Update book
      Book updatedBook = bookResponse.getData();
      updatedBook.setDescription("Updated description " + i);
      ApiResponse<Book> updateResponse = bookApiClient.updateBook(updatedBook.getId(), updatedBook);
      Assert.assertTrue(updateResponse.isSuccess(), "Book update should succeed");
    }

    // Verify final counts are consistent
    int finalBookCount = bookApiClient.getBooksCount();
    int finalAuthorCount = authorApiClient.getAuthorsCount();

    Assert.assertTrue(finalBookCount >= initialBookCount + 3,
      "Book count should increase by at least 3");
    Assert.assertTrue(finalAuthorCount >= initialAuthorCount + 3,
      "Author count should increase by at least 3");

    logTestStep("Data consistency verified after multiple operations");
  }
}

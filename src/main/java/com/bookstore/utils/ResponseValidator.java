package com.bookstore.utils;

import com.bookstore.models.ApiResponse;
import com.bookstore.models.Book;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

@Slf4j
public class ResponseValidator {

  public static void validateSuccessfulResponse(ApiResponse<?> response) {
    Assert.assertNotNull(response, "Response should not be null");
    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300,
      "Status code should be in 2xx range");
  }

  public static void validateErrorResponse(ApiResponse<?> response, int expectedStatusCode) {
    Assert.assertNotNull(response, "Response should not be null");
    Assert.assertFalse(response.isSuccess(), "Response should not be successful");
    Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
      "Status code should match expected error code");
  }

  public static void validateBookResponse(ApiResponse<Book> response) {
    validateSuccessfulResponse(response);
    Assert.assertNotNull(response.getData(), "Book data should not be null");

    Book book = response.getData();
    Assert.assertNotNull(book.getId(), "Book ID should not be null");
    Assert.assertNotNull(book.getTitle(), "Book title should not be null");
    Assert.assertFalse(book.getTitle().trim().isEmpty(), "Book title should not be empty");
  }

  public static void validateBookListResponse(ApiResponse<List<Book>> response) {
    validateSuccessfulResponse(response);
    Assert.assertNotNull(response.getData(), "Book list should not be null");

    List<Book> books = response.getData();
    books.forEach(book -> {
      Assert.assertNotNull(book.getId(), "Each book should have an ID");
      Assert.assertNotNull(book.getTitle(), "Each book should have a title");
    });
  }

  public static void validateResponseTime(ApiResponse<?> response, long maxTimeMs) {
    Assert.assertTrue(response.getResponseTime() <= maxTimeMs,
      String.format("Response time should be <= %d ms, actual: %d ms",
        maxTimeMs, response.getResponseTime()));
  }

  public static void validateBookFields(Book expected, Book actual) {
    Assert.assertEquals(actual.getTitle(), expected.getTitle(), "Title should match");
    Assert.assertEquals(actual.getDescription(), expected.getDescription(), "Description should match");
    Assert.assertEquals(actual.getPageCount(), expected.getPageCount(), "Page count should match");
    Assert.assertEquals(actual.getExcerpt(), expected.getExcerpt(), "Excerpt should match");
  }
}

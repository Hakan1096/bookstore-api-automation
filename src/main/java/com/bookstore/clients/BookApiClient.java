package com.bookstore.clients;

import com.bookstore.models.ApiResponse;
import com.bookstore.models.Book;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BookApiClient extends BaseApiClient {

  @Step("Get all books")
  public ApiResponse<List<Book>> getAllBooks() {
    log.info("Getting all books from: {}", config.getBooksEndpoint());
    return executeGet(config.getBooksEndpoint(),
      (Class<List<Book>>) (Class<?>) List.class);
  }

  @Step("Get book by ID: {id}")
  public ApiResponse<Book> getBookById(int id) {
    log.info("Getting book with ID: {}", id);
    String endpoint = config.getBookByIdEndpoint(id);
    return executeGet(endpoint, Book.class);
  }

  @Step("Create new book")
  public ApiResponse<Book> createBook(Book book) {
    log.info("Creating new book: {}", book.getTitle());
    return executePost(config.getBooksEndpoint(), book, Book.class);
  }

  @Step("Update book with ID: {id}")
  public ApiResponse<Book> updateBook(int id, Book book) {
    log.info("Updating book with ID: {}", id);
    String endpoint = config.getBookByIdEndpoint(id);
    return executePut(endpoint, book, Book.class);
  }

  @Step("Delete book with ID: {id}")
  public ApiResponse<Void> deleteBook(int id) {
    log.info("Deleting book with ID: {}", id);
    String endpoint = config.getBookByIdEndpoint(id);
    return executeDelete(endpoint, Void.class);
  }

  @Step("Partially update book with ID: {id}")
  public ApiResponse<Book> partialUpdateBook(int id, Book book) {
    log.info("Partially updating book with ID: {}", id);
    String endpoint = config.getBookByIdEndpoint(id);
    return executePatch(endpoint, book, Book.class);
  }

  // Additional utility methods for testing
  @Step("Verify book exists with ID: {id}")
  public boolean bookExists(int id) {
    ApiResponse<Book> response = getBookById(id);
    return response.isSuccess() && response.getData() != null;
  }

  @Step("Get books count")
  public int getBooksCount() {
    ApiResponse<List<Book>> response = getAllBooks();
    if (response.isSuccess() && response.getData() != null) {
      return response.getData().size();
    }
    return 0;
  }

  @Step("Create book and verify creation")
  public Book createAndVerifyBook(Book book) {
    ApiResponse<Book> createResponse = createBook(book);

    if (!createResponse.isSuccess()) {
      throw new RuntimeException("Failed to create book: " + createResponse.getStatusMessage());
    }

    Book createdBook = createResponse.getData();
    if (createdBook == null || createdBook.getId() == null) {
      throw new RuntimeException("Created book data is invalid");
    }

    // Verify the book was actually created by fetching it
    ApiResponse<Book> fetchResponse = getBookById(createdBook.getId());
    if (!fetchResponse.isSuccess()) {
      throw new RuntimeException("Failed to verify book creation");
    }

    return createdBook;
  }

  @Step("Update book and verify update")
  public Book updateAndVerifyBook(int id, Book book) {
    ApiResponse<Book> updateResponse = updateBook(id, book);

    if (!updateResponse.isSuccess()) {
      throw new RuntimeException("Failed to update book: " + updateResponse.getStatusMessage());
    }

    // Verify the update by fetching the book
    ApiResponse<Book> fetchResponse = getBookById(id);
    if (!fetchResponse.isSuccess()) {
      throw new RuntimeException("Failed to verify book update");
    }

    return fetchResponse.getData();
  }

  @Step("Delete book and verify deletion")
  public void deleteAndVerifyBook(int id) {
    ApiResponse<Void> deleteResponse = deleteBook(id);

    if (!deleteResponse.isSuccess()) {
      throw new RuntimeException("Failed to delete book: " + deleteResponse.getStatusMessage());
    }

    // Verify the book was actually deleted
    ApiResponse<Book> fetchResponse = getBookById(id);
    if (fetchResponse.isSuccess()) {
      throw new RuntimeException("Book still exists after deletion");
    }
  }
}

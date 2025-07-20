package com.bookstore.clients;

import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AuthorApiClient extends BaseApiClient {

  @Step("Get all authors")
  public ApiResponse<List<Author>> getAllAuthors() {
    log.info("Getting all authors from: {}", config.getAuthorsEndpoint());
    return executeGet(config.getAuthorsEndpoint(),
      (Class<List<Author>>) (Class<?>) List.class);
  }

  @Step("Get author by ID: {id}")
  public ApiResponse<Author> getAuthorById(int id) {
    log.info("Getting author with ID: {}", id);
    String endpoint = config.getAuthorByIdEndpoint(id);
    return executeGet(endpoint, Author.class);
  }

  @Step("Create new author")
  public ApiResponse<Author> createAuthor(Author author) {
    log.info("Creating new author: {} {}", author.getFirstName(), author.getLastName());
    return executePost(config.getAuthorsEndpoint(), author, Author.class);
  }

  @Step("Update author with ID: {id}")
  public ApiResponse<Author> updateAuthor(int id, Author author) {
    log.info("Updating author with ID: {}", id);
    String endpoint = config.getAuthorByIdEndpoint(id);
    return executePut(endpoint, author, Author.class);
  }

  @Step("Delete author with ID: {id}")
  public ApiResponse<Void> deleteAuthor(int id) {
    log.info("Deleting author with ID: {}", id);
    String endpoint = config.getAuthorByIdEndpoint(id);
    return executeDelete(endpoint, Void.class);
  }

  @Step("Partially update author with ID: {id}")
  public ApiResponse<Author> partialUpdateAuthor(int id, Author author) {
    log.info("Partially updating author with ID: {}", id);
    String endpoint = config.getAuthorByIdEndpoint(id);
    return executePatch(endpoint, author, Author.class);
  }

  // Additional utility methods for testing
  @Step("Verify author exists with ID: {id}")
  public boolean authorExists(int id) {
    ApiResponse<Author> response = getAuthorById(id);
    return response.isSuccess() && response.getData() != null;
  }

  @Step("Get authors count")
  public int getAuthorsCount() {
    ApiResponse<List<Author>> response = getAllAuthors();
    if (response.isSuccess() && response.getData() != null) {
      return response.getData().size();
    }
    return 0;
  }

  @Step("Find authors by book ID: {bookId}")
  public List<Author> getAuthorsByBookId(int bookId) {
    ApiResponse<List<Author>> response = getAllAuthors();

    if (!response.isSuccess() || response.getData() == null) {
      throw new RuntimeException("Failed to get authors: " + response.getStatusMessage());
    }

    return response.getData().stream()
      .filter(author -> author.getIdBook() != null && author.getIdBook().equals(bookId))
      .collect(Collectors.toList());
  }

  @Step("Create author and verify creation")
  public Author createAndVerifyAuthor(Author author) {
    ApiResponse<Author> createResponse = createAuthor(author);

    if (!createResponse.isSuccess()) {
      throw new RuntimeException("Failed to create author: " + createResponse.getStatusMessage());
    }

    Author createdAuthor = createResponse.getData();
    if (createdAuthor == null || createdAuthor.getId() == null) {
      throw new RuntimeException("Created author data is invalid");
    }

    // Verify the author was actually created by fetching it
    ApiResponse<Author> fetchResponse = getAuthorById(createdAuthor.getId());
    if (!fetchResponse.isSuccess()) {
      throw new RuntimeException("Failed to verify author creation");
    }

    return createdAuthor;
  }

  @Step("Update author and verify update")
  public Author updateAndVerifyAuthor(int id, Author author) {
    ApiResponse<Author> updateResponse = updateAuthor(id, author);

    if (!updateResponse.isSuccess()) {
      throw new RuntimeException("Failed to update author: " + updateResponse.getStatusMessage());
    }

    // Verify the update by fetching the author
    ApiResponse<Author> fetchResponse = getAuthorById(id);
    if (!fetchResponse.isSuccess()) {
      throw new RuntimeException("Failed to verify author update");
    }

    return fetchResponse.getData();
  }

  @Step("Delete author and verify deletion")
  public void deleteAndVerifyAuthor(int id) {
    ApiResponse<Void> deleteResponse = deleteAuthor(id);

    if (!deleteResponse.isSuccess()) {
      throw new RuntimeException("Failed to delete author: " + deleteResponse.getStatusMessage());
    }

    // Verify the author was actually deleted
    ApiResponse<Author> fetchResponse = getAuthorById(id);
    if (fetchResponse.isSuccess()) {
      throw new RuntimeException("Author still exists after deletion");
    }
  }
}
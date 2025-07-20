package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class FileUtils {
  private static final String TEST_DATA_PATH = "test-data/";

  public static String readFileFromResources(String fileName) {
    try (InputStream inputStream = FileUtils.class.getClassLoader()
      .getResourceAsStream(TEST_DATA_PATH + fileName)) {

      if (inputStream == null) {
        throw new IllegalArgumentException("File not found: " + fileName);
      }

      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error("Error reading file {}: {}", fileName, e.getMessage());
      throw new RuntimeException("Failed to read file: " + fileName, e);
    }
  }

  public static Book loadBookFromFile(String fileName) {
    String jsonContent = readFileFromResources(fileName);
    return JsonUtils.fromJson(jsonContent, Book.class);
  }

  public static Author loadAuthorFromFile(String fileName) {
    String jsonContent = readFileFromResources(fileName);
    return JsonUtils.fromJson(jsonContent, Author.class);
  }

  public static List<Book> loadBooksFromFile(String fileName) {
    String jsonContent = readFileFromResources(fileName);
    return JsonUtils.fromJson(jsonContent, new TypeReference<List<Book>>() {});
  }

  public static List<Author> loadAuthorsFromFile(String fileName) {
    String jsonContent = readFileFromResources(fileName);
    return JsonUtils.fromJson(jsonContent, new TypeReference<List<Author>>() {});
  }

  // Predefined test data loaders
  public static Book getValidBook() {
    return loadBookFromFile("valid-book.json");
  }

  public static Book getInvalidBook() {
    return loadBookFromFile("invalid-book.json");
  }

  public static Book getBookWithSpecialChars() {
    return loadBookFromFile("book-with-special-chars.json");
  }

  public static Book getMinimalBook() {
    return loadBookFromFile("book-minimal.json");
  }

  public static Book getBookWithLargeFields() {
    return loadBookFromFile("book-large-fields.json");
  }

  public static Author getValidAuthor() {
    return loadAuthorFromFile("valid-author.json");
  }

  public static Author getInvalidAuthor() {
    return loadAuthorFromFile("invalid-author.json");
  }

  public static Author getAuthorWithSpecialChars() {
    return loadAuthorFromFile("author-with-special-chars.json");
  }

  public static List<Book> getBulkBooks() {
    return loadBooksFromFile("bulk-books.json");
  }

  public static List<Author> getBulkAuthors() {
    return loadAuthorsFromFile("bulk-authors.json");
  }
}


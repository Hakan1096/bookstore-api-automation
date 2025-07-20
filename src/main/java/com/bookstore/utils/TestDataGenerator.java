package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TestDataGenerator {
  private static final Faker faker = new Faker();
  private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  // Book test data generators
  public static Book generateValidBook() {
    return Book.builder()
      .title(faker.book().title())
      .description(faker.lorem().paragraph())
      .pageCount(faker.number().numberBetween(50, 1000))
      .excerpt(faker.lorem().sentence())
      .publishDate(generateRandomDate())
      .build();
  }

  public static Book generateBookWithLongFields() {
    return Book.builder()
      .title(faker.lorem().characters(100))
      .description(faker.lorem().characters(500))
      .pageCount(9999)
      .excerpt(faker.lorem().characters(200))
      .publishDate(generateRandomDate())
      .build();
  }

  public static Book generateBookWithSpecialCharacters() {
    return Book.builder()
      .title("Test Book !@#$%^&*()_+-={}[]|\\:;\"'<>?,./")
      .description("Description with special chars: àáâãäåæçèéêë")
      .pageCount(100)
      .excerpt("Excerpt with émojis: 📚📖📝")
      .publishDate(generateRandomDate())
      .build();
  }

  public static Book generateBookWithMinimalFields() {
    return Book.builder()
      .title("Minimal Book")
      .pageCount(1)
      .build();
  }

  public static Book generateInvalidBook() {
    return Book.builder()
      .title("") // Empty title
      .pageCount(-1) // Invalid page count
      .build();
  }

  // Author test data generators
  public static Author generateValidAuthor() {
    return Author.builder()
      .firstName(faker.name().firstName())
      .lastName(faker.name().lastName())
      .idBook(faker.number().numberBetween(1, 100))
      .build();
  }

  public static Author generateAuthorWithLongNames() {
    return Author.builder()
      .firstName(faker.lorem().characters(50))
      .lastName(faker.lorem().characters(50))
      .idBook(faker.number().numberBetween(1, 100))
      .build();
  }

  public static Author generateAuthorWithSpecialCharacters() {
    return Author.builder()
      .firstName("José María")
      .lastName("García-López")
      .idBook(faker.number().numberBetween(1, 100))
      .build();
  }

  public static Author generateInvalidAuthor() {
    return Author.builder()
      .firstName("") // Empty first name
      .lastName("") // Empty last name
      .idBook(-1) // Invalid book ID
      .build();
  }

  // Utility methods
  public static List<Book> generateBookList(int count) {
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      books.add(generateValidBook());
    }
    return books;
  }

  public static List<Author> generateAuthorList(int count) {
    List<Author> authors = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      authors.add(generateValidAuthor());
    }
    return authors;
  }

  public static String generateRandomDate() {
    LocalDateTime randomDate = LocalDateTime.now()
      .minusDays(ThreadLocalRandom.current().nextInt(0, 365 * 5))
      .plusDays(ThreadLocalRandom.current().nextInt(0, 365 * 2));
    return randomDate.format(ISO_FORMATTER);
  }

  public static int generateRandomId() {
    return faker.number().numberBetween(1, 200);
  }

  public static int generateInvalidId() {
    return faker.number().numberBetween(-100, 0);
  }

  public static int generateNonExistentId() {
    return faker.number().numberBetween(9999, 99999);
  }
}
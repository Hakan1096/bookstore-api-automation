package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("pageCount")
  private Integer pageCount;

  @JsonProperty("excerpt")
  private String excerpt;

  @JsonProperty("publishDate")
  private String publishDate;

  // Builder methods for fluent API
  public static class BookBuilder {
    public BookBuilder withValidData() {
      return this.title("Sample Book Title")
        .description("Sample book description")
        .pageCount(250)
        .excerpt("Sample excerpt from the book")
        .publishDate("2024-01-01T00:00:00.000Z");
    }

    public BookBuilder withMinimalData() {
      return this.title("Minimal Book")
        .pageCount(1);
    }

    public BookBuilder withLargeData() {
      return this.title("A".repeat(100))
        .description("B".repeat(500))
        .pageCount(9999)
        .excerpt("C".repeat(200))
        .publishDate("2024-12-31T23:59:59.999Z");
    }
  }

  // Validation methods
  public boolean isValid() {
    return title != null && !title.trim().isEmpty() && pageCount != null && pageCount > 0;
  }

  public boolean hasRequiredFields() {
    return title != null && pageCount != null;
  }
}
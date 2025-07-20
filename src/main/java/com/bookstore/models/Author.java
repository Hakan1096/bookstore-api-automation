package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("idBook")
  private Integer idBook;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  // Builder methods for fluent API
  public static class AuthorBuilder {
    public AuthorBuilder withValidData() {
      return this.firstName("John")
        .lastName("Doe")
        .idBook(1);
    }

    public AuthorBuilder withMinimalData() {
      return this.firstName("Jane")
        .lastName("Smith");
    }

    public AuthorBuilder withLongNames() {
      return this.firstName("A".repeat(50))
        .lastName("B".repeat(50))
        .idBook(999);
    }
  }

  // Utility methods
  public String getFullName() {
    return firstName + " " + lastName;
  }

  public boolean isValid() {
    return firstName != null && !firstName.trim().isEmpty() &&
      lastName != null && !lastName.trim().isEmpty();
  }

  public boolean hasRequiredFields() {
    return firstName != null && lastName != null;
  }
}


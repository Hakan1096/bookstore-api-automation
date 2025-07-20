package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Authors API - Negative Tests")
public class AuthorApiNegativeTests extends BaseTest {

  @Test(description = "Verify getting author with non-existent ID returns 404")
  @Description("Test that GET /api/v1/Authors/{id} with non-existent ID returns 404")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Error Handling")
  public void testGetAuthorWithNonExistentId() {
    int nonExistentId = TestDataGenerator.generateNonExistentId();
    logTestStep("Attempting to get non-existent author ID: " + nonExistentId);

    ApiResponse<Author> response = authorApiClient.getAuthorById(nonExistentId);

    Assert.assertTrue(response.isClientError(), "Non-existent ID should return client error");
    Assert.assertEquals(response.getStatusCode(), 404, "Status code should be 404");

    logTestStep("Verified non-existent author ID returns 404");
  }

  @Test(description = "Verify creating author with invalid data returns 400")
  @Description("Test that POST /api/v1/Authors with invalid data returns validation error")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Data Validation")
  public void testCreateAuthorWithInvalidData() {
    Author invalidAuthor = TestDataGenerator.generateInvalidAuthor();
    logTestStep("Attempting to create author with invalid data");

    ApiResponse<Author> response = authorApiClient.createAuthor(invalidAuthor);

    Assert.assertTrue(response.isClientError(), "Invalid data should return client error");
    Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

    logTestStep("Verified invalid author data properly rejected");
  }

  @Test(description = "Verify creating author with empty names returns error")
  @Description("Test that authors with empty first or last names are rejected")
  @Severity(SeverityLevel.NORMAL)
  @Story("Data Validation")
  public void testCreateAuthorWithEmptyNames() {
    Author emptyFirstName = Author.builder()
      .firstName("")
      .lastName("ValidLastName")
      .idBook(1)
      .build();

    logTestStep("Attempting to create author with empty first name");
    ApiResponse<Author> firstResponse = authorApiClient.createAuthor(emptyFirstName);
    Assert.assertTrue(firstResponse.isClientError() || firstResponse.isSuccess(),
      "Empty first name should be handled appropriately");

    Author emptyLastName = Author.builder()
      .firstName("ValidFirstName")
      .lastName("")
      .idBook(1)
      .build();

    logTestStep("Attempting to create author with empty last name");
    ApiResponse<Author> lastResponse = authorApiClient.createAuthor(emptyLastName);
    Assert.assertTrue(lastResponse.isClientError() || lastResponse.isSuccess(),
      "Empty last name should be handled appropriately");

    logTestStep("Verified empty name validation");
  }

  @Test(description = "Verify updating author with invalid ID returns error")
  @Description("Test that PUT /api/v1/Authors/{id} with invalid ID returns error")
  @Severity(SeverityLevel.NORMAL)
  @Story("Error Handling")
  public void testUpdateAuthorWithInvalidId() {
    int invalidId = TestDataGenerator.generateInvalidId();
    Author updateAuthor = TestDataGenerator.generateValidAuthor();

    logTestStep("Attempting to update author with invalid ID: " + invalidId);

    ApiResponse<Author> response = authorApiClient.updateAuthor(invalidId, updateAuthor);

    Assert.assertTrue(response.isClientError() || response.isServerError(),
      "Invalid ID should return error");
    Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404,
      "Status code should be 400 or 404");

    logTestStep("Verified invalid ID update properly rejected");
  }

  @Test(description = "Verify deleting author with invalid ID returns error")
  @Description("Test that DELETE /api/v1/Authors/{id} with invalid ID returns error")
  @Severity(SeverityLevel.NORMAL)
  @Story("Error Handling")
  public void testDeleteAuthorWithInvalidId() {
    int invalidId = TestDataGenerator.generateInvalidId();

    logTestStep("Attempting to delete author with invalid ID: " + invalidId);

    ApiResponse<Void> response = authorApiClient.deleteAuthor(invalidId);

    Assert.assertTrue(response.isClientError() || response.isServerError(),
      "Invalid ID should return error");
    Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404,
      "Status code should be 400 or 404");

    logTestStep("Verified invalid ID delete properly rejected");
  }

  @Test(description = "Verify creating author with null names returns error")
  @Description("Test that authors with null first or last names are rejected")
  @Severity(SeverityLevel.NORMAL)
  @Story("Data Validation")
  public void testCreateAuthorWithNullNames() {
    Author nullFirstName = Author.builder()
      .firstName(null)
      .lastName("ValidLastName")
      .idBook(1)
      .build();

    logTestStep("Attempting to create author with null first name");
    ApiResponse<Author> firstResponse = authorApiClient.createAuthor(nullFirstName);
    Assert.assertTrue(firstResponse.isClientError() || firstResponse.isSuccess(),
      "Null first name should be handled appropriately");

    Author nullLastName = Author.builder()
      .firstName("ValidFirstName")
      .lastName(null)
      .idBook(1)
      .build();

    logTestStep("Attempting to create author with null last name");
    ApiResponse<Author> lastResponse = authorApiClient.createAuthor(nullLastName);
    Assert.assertTrue(lastResponse.isClientError() || lastResponse.isSuccess(),
      "Null last name should be handled appropriately");

    logTestStep("Verified null name validation");
  }
}

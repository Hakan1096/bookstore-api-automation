package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Authors API - Happy Path Tests")
public class AuthorApiHappyPathTests extends BaseTest {

  @Test(description = "Verify getting all authors returns successful response")
  @Description("Test to verify that GET /api/v1/Authors returns a list of authors with 200 status")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Get All Authors")
  public void testGetAllAuthors() {
    logTestStep("Getting all authors from the API");

    ApiResponse<List<Author>> response = authorApiClient.getAllAuthors();

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    Assert.assertNotNull(response.getData(), "Response data should not be null");

    logTestStep("Verified all authors retrieved successfully");
  }

  @Test(description = "Verify getting a specific author by valid ID")
  @Description("Test to verify that GET /api/v1/Authors/{id} returns author details for valid ID")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Get Author by ID")
  public void testGetAuthorByValidId() {
    int authorId = 1;
    logTestStep("Getting author with ID: " + authorId);

    ApiResponse<Author> response = authorApiClient.getAuthorById(authorId);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    Assert.assertNotNull(response.getData(), "Author data should not be null");
    Assert.assertEquals(response.getData().getId(), authorId, "Author ID should match requested ID");

    logTestStep("Verified author retrieved successfully by ID");
  }

  @Test(description = "Verify creating a new author with valid data")
  @Description("Test to verify that POST /api/v1/Authors creates a new author successfully")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Create Author")
  public void testCreateAuthorWithValidData() {
    Author newAuthor = TestDataGenerator.generateValidAuthor();
    logTestStep("Creating new author: " + newAuthor.getFullName());

    ApiResponse<Author> response = authorApiClient.createAuthor(newAuthor);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
      "Status code should be 200 or 201");
    Assert.assertNotNull(response.getData(), "Created author data should not be null");
    Assert.assertNotNull(response.getData().getId(), "Created author should have an ID");
    Assert.assertEquals(response.getData().getFirstName(), newAuthor.getFirstName(),
      "Author first name should match");
    Assert.assertEquals(response.getData().getLastName(), newAuthor.getLastName(),
      "Author last name should match");

    logTestStep("Verified author created successfully");
  }

  @Test(description = "Verify updating an existing author")
  @Description("Test to verify that PUT /api/v1/Authors/{id} updates author successfully")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Update Author")
  public void testUpdateExistingAuthor() {
    int authorId = 1;
    Author updatedAuthor = TestDataGenerator.generateValidAuthor();
    updatedAuthor.setId(authorId);

    logTestStep("Updating author with ID: " + authorId);

    ApiResponse<Author> response = authorApiClient.updateAuthor(authorId, updatedAuthor);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    Assert.assertNotNull(response.getData(), "Updated author data should not be null");
    Assert.assertEquals(response.getData().getId(), authorId, "Author ID should match");

    logTestStep("Verified author updated successfully");
  }

  @Test(description = "Verify deleting an existing author")
  @Description("Test to verify that DELETE /api/v1/Authors/{id} deletes author successfully")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Delete Author")
  public void testDeleteExistingAuthor() {
    // First create an author to delete
    Author authorToDelete = TestDataGenerator.generateValidAuthor();
    ApiResponse<Author> createResponse = authorApiClient.createAuthor(authorToDelete);

    Assert.assertTrue(createResponse.isSuccess(), "Author creation should be successful");
    int authorId = createResponse.getData().getId();

    logTestStep("Deleting author with ID: " + authorId);

    ApiResponse<Void> deleteResponse = authorApiClient.deleteAuthor(authorId);

    Assert.assertTrue(deleteResponse.isSuccess(), "Delete response should be successful");
    Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Status code should be 200");

    logTestStep("Verified author deleted successfully");
  }

  @Test(description = "Verify creating author with minimal required fields")
  @Description("Test to verify that an author can be created with only required fields")
  @Severity(SeverityLevel.NORMAL)
  @Story("Create Author")
  public void testCreateAuthorWithMinimalFields() {
    Author minimalAuthor = TestDataGenerator.generateAuthorWithMinimalData();
    logTestStep("Creating author with minimal fields: " + minimalAuthor.getFullName());

    ApiResponse<Author> response = authorApiClient.createAuthor(minimalAuthor);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertNotNull(response.getData(), "Created author data should not be null");
    Assert.assertEquals(response.getData().getFirstName(), minimalAuthor.getFirstName(),
      "Author first name should match");
    Assert.assertEquals(response.getData().getLastName(), minimalAuthor.getLastName(),
      "Author last name should match");

    logTestStep("Verified author with minimal fields created successfully");
  }

  @Test(description = "Verify creating author with book association")
  @Description("Test to verify that an author can be created with book ID association")
  @Severity(SeverityLevel.NORMAL)
  @Story("Create Author")
  public void testCreateAuthorWithBookAssociation() {
    Author authorWithBook = TestDataGenerator.generateValidAuthor();
    authorWithBook.setIdBook(1); // Associate with book ID 1

    logTestStep("Creating author with book association: " + authorWithBook.getFullName());

    ApiResponse<Author> response = authorApiClient.createAuthor(authorWithBook);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertNotNull(response.getData(), "Created author data should not be null");
    Assert.assertEquals(response.getData().getIdBook(), authorWithBook.getIdBook(),
      "Book association should match");

    logTestStep("Verified author with book association created successfully");
  }
}

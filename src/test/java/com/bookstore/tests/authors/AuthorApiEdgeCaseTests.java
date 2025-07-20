package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Authors API - Edge Case Tests")
public class AuthorApiEdgeCaseTests extends BaseTest {

  @Test(description = "Verify creating author with special characters in names")
  @Description("Test creating author with special characters and international names")
  @Severity(SeverityLevel.NORMAL)
  @Story("Special Character Testing")
  public void testCreateAuthorWithSpecialCharacters() {
    Author specialCharAuthor = TestDataGenerator.generateAuthorWithSpecialCharacters();
    logTestStep("Creating author with special characters: " + specialCharAuthor.getFullName());

    ApiResponse<Author> response = authorApiClient.createAuthor(specialCharAuthor);

    Assert.assertTrue(response.isSuccess() || response.isClientError(),
      "Response should handle special characters appropriately");

    if (response.isSuccess()) {
      Assert.assertNotNull(response.getData(), "Created author should not be null");
      Assert.assertEquals(response.getData().getFirstName(), specialCharAuthor.getFirstName(),
        "Special characters in first name should be preserved");
      Assert.assertEquals(response.getData().getLastName(), specialCharAuthor.getLastName(),
        "Special characters in last name should be preserved");
    }

    logTestStep("Verified special character handling in author names");
  }

  @Test(description = "Verify creating author with very long names")
  @Description("Test creating author with maximum length names")
  @Severity(SeverityLevel.NORMAL)
  @Story("Field Length Testing")
  public void testCreateAuthorWithLongNames() {
    Author longNameAuthor = TestDataGenerator.generateAuthorWithLongNames();
    logTestStep("Creating author with long names");

    ApiResponse<Author> response = authorApiClient.createAuthor(longNameAuthor);

    Assert.assertTrue(response.isSuccess() || response.isClientError(),
      "Response should handle long names appropriately");

    if (response.isSuccess()) {
      Assert.assertNotNull(response.getData(), "Created author should not be null");
    }

    logTestStep("Verified long name handling");
  }

  @Test(description = "Verify author with boundary book ID values")
  @Description("Test creating author with edge case book ID values")
  @Severity(SeverityLevel.NORMAL)
  @Story("Boundary Value Testing")
  public void testCreateAuthorWithBoundaryBookIds() {
    // Test with book ID = 0
    Author authorWithZeroBookId = TestDataGenerator.generateValidAuthor();
    authorWithZeroBookId.setIdBook(0);

    logTestStep("Creating author with book ID = 0");
    ApiResponse<Author> zeroResponse = authorApiClient.createAuthor(authorWithZeroBookId);
    Assert.assertTrue(zeroResponse.isSuccess() || zeroResponse.isClientError(),
      "Zero book ID should be handled appropriately");

    // Test with negative book ID
    Author authorWithNegativeBookId = TestDataGenerator.generateValidAuthor();
    authorWithNegativeBookId.setIdBook(-1);

    logTestStep("Creating author with negative book ID");
    ApiResponse<Author> negativeResponse = authorApiClient.createAuthor(authorWithNegativeBookId);
    Assert.assertTrue(negativeResponse.isSuccess() || negativeResponse.isClientError(),
      "Negative book ID should be handled appropriately");

    logTestStep("Verified boundary book ID handling");
  }

  @Test(description = "Verify updating author with partial data")
  @Description("Test updating author with only some fields")
  @Severity(SeverityLevel.NORMAL)
  @Story("Update Author")
  public void testPartialUpdateAuthor() {
    int authorId = 2;
    Author partialUpdate = Author.builder()
      .id(authorId)
      .firstName("UpdatedFirstName")
      .build(); // Only updating first name

    logTestStep("Performing partial update for author ID: " + authorId);

    ApiResponse<Author> response = authorApiClient.updateAuthor(authorId, partialUpdate);

    Assert.assertTrue(response.isSuccess(), "Response should be successful");
    Assert.assertNotNull(response.getData(), "Updated author data should not be null");
    Assert.assertEquals(response.getData().getFirstName(), partialUpdate.getFirstName(),
      "First name should be updated");

    logTestStep("Verified partial author update successful");
  }

  @Test(description = "Verify handling of duplicate author names")
  @Description("Test creating authors with identical names")
  @Severity(SeverityLevel.NORMAL)
  @Story("Duplicate Data Testing")
  public void testCreateDuplicateAuthorNames() {
    Author firstAuthor = Author.builder()
      .firstName("John")
      .lastName("Duplicate")
      .idBook(1)
      .build();

    Author secondAuthor = Author.builder()
      .firstName("John")
      .lastName("Duplicate")
      .idBook(2)
      .build();

    logTestStep("Creating first author with name: " + firstAuthor.getFullName());
    ApiResponse<Author> firstResponse = authorApiClient.createAuthor(firstAuthor);
    Assert.assertTrue(firstResponse.isSuccess(), "First author creation should succeed");

    logTestStep("Creating second author with same name: " + secondAuthor.getFullName());
    ApiResponse<Author> secondResponse = authorApiClient.createAuthor(secondAuthor);

    // Should either succeed (duplicates allowed) or fail with appropriate error
    Assert.assertTrue(secondResponse.isSuccess() || secondResponse.isClientError(),
      "Duplicate names should be handled appropriately");

    logTestStep("Verified duplicate name handling");
  }
}

package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.models.ApiResponse;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

@Feature("Books API - Happy Path Tests")
public class BookApiHappyPathTests extends BaseTest {
    
    @Test(description = "Verify getting all books returns successful response")
    @Description("Test to verify that GET /api/v1/Books returns a list of books with 200 status")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get All Books")
    public void testGetAllBooks() {
        logTestStep("Getting all books from the API");
        
        ApiResponse<List<Book>> response = bookApiClient.getAllBooks();
        
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertNotNull(response.getData(), "Response data should not be null");
        
        logTestStep("Verified all books retrieved successfully");
    }
    
    @Test(description = "Verify getting a specific book by valid ID")
    @Description("Test to verify that GET /api/v1/Books/{id} returns book details for valid ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Book by ID")
    public void testGetBookByValidId() {
        int bookId = 1;
        logTestStep("Getting book with ID: " + bookId);
        
        ApiResponse<Book> response = bookApiClient.getBookById(bookId);
        
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertNotNull(response.getData(), "Book data should not be null");
        
        logTestStep("Verified book retrieved successfully by ID");
    }
    
    @Test(description = "Verify creating a new book with valid data")
    @Description("Test to verify that POST /api/v1/Books creates a new book successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Book")
    public void testCreateBookWithValidData() {
        Book newBook = TestDataGenerator.generateValidBook();
        logTestStep("Creating new book: " + newBook.getTitle());
        
        ApiResponse<Book> response = bookApiClient.createBook(newBook);
        
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        Assert.assertNotNull(response.getData(), "Created book data should not be null");
        
        logTestStep("Verified book created successfully");
    }
    
    @Test(description = "Verify updating an existing book")
    @Description("Test to verify that PUT /api/v1/Books/{id} updates book successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Book")
    public void testUpdateExistingBook() {
        int bookId = 1;
        Book updatedBook = TestDataGenerator.generateValidBook();
        updatedBook.setId(bookId);
        
        logTestStep("Updating book with ID: " + bookId);
        
        ApiResponse<Book> response = bookApiClient.updateBook(bookId, updatedBook);
        
        Assert.assertTrue(response.isSuccess(), "Response should be successful");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        logTestStep("Verified book updated successfully");
    }
    
    @Test(description = "Verify deleting an existing book")
    @Description("Test to verify that DELETE /api/v1/Books/{id} deletes book successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Book")
    public void testDeleteExistingBook() {
        Book bookToDelete = TestDataGenerator.generateValidBook();
        ApiResponse<Book> createResponse = bookApiClient.createBook(bookToDelete);
        
        Assert.assertTrue(createResponse.isSuccess(), "Book creation should be successful");
        int bookId = createResponse.getData().getId();
        
        logTestStep("Deleting book with ID: " + bookId);
        
        ApiResponse<Void> deleteResponse = bookApiClient.deleteBook(bookId);
        
        Assert.assertTrue(deleteResponse.isSuccess(), "Delete response should be successful");
        
        logTestStep("Verified book deleted successfully");
    }
}

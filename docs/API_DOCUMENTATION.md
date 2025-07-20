# Bookstore API Documentation

## 📚 Overview

The Bookstore API provides a comprehensive RESTful interface for managing books and authors in an online bookstore system. This API supports full CRUD operations for both books and authors, with proper HTTP status codes and JSON responses.

**Base URL:** `https://fakerestapi.azurewebsites.net`

**API Version:** v1

**Response Format:** JSON

**Last Updated:** July 2025

## 🔐 Authentication

Currently, the Bookstore API does not require authentication. All endpoints are publicly accessible for testing and development purposes.

> **Note:** In production environments, proper authentication mechanisms (API keys, OAuth 2.0, JWT tokens) should be implemented.

## 📖 Books API

### Base Endpoint
```
/api/v1/Books
```

### Data Model

#### Book Object
```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "pageCount": 0,
  "excerpt": "string",
  "publishDate": "2024-01-01T00:00:00.000Z"
}
```

#### Field Descriptions
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | integer | No (auto-generated) | Unique identifier for the book |
| `title` | string | Yes | Title of the book |
| `description` | string | No | Detailed description of the book |
| `pageCount` | integer | Yes | Number of pages in the book |
| `excerpt` | string | No | Short excerpt or summary |
| `publishDate` | string (ISO 8601) | No | Publication date in ISO format |

### Endpoints

#### 1. Get All Books
```http
GET /api/v1/Books
```

**Description:** Retrieves a list of all books in the system.

**Response:**
```json
[
  {
    "id": 1,
    "title": "To Kill a Mockingbird",
    "description": "A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.",
    "pageCount": 324,
    "excerpt": "When he was nearly thirteen, my brother Jem got his arm badly broken at the elbow.",
    "publishDate": "1960-07-11T00:00:00.000Z"
  },
  {
    "id": 2,
    "title": "1984",
    "description": "A dystopian social science fiction novel and cautionary tale.",
    "pageCount": 328,
    "excerpt": "It was a bright cold day in April, and the clocks were striking thirteen.",
    "publishDate": "1949-06-08T00:00:00.000Z"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `500 Internal Server Error` - Server error

---

#### 2. Get Book by ID
```http
GET /api/v1/Books/{id}
```

**Description:** Retrieves a specific book by its unique identifier.

**Parameters:**
| Parameter | Type | Location | Required | Description |
|-----------|------|----------|----------|-------------|
| `id` | integer | path | Yes | Unique book identifier |

**Example Request:**
```http
GET /api/v1/Books/1
```

**Example Response:**
```json
{
  "id": 1,
  "title": "To Kill a Mockingbird",
  "description": "A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.",
  "pageCount": 324,
  "excerpt": "When he was nearly thirteen, my brother Jem got his arm badly broken at the elbow.",
  "publishDate": "1960-07-11T00:00:00.000Z"
}
```

**Status Codes:**
- `200 OK` - Book found and returned
- `404 Not Found` - Book with specified ID not found
- `400 Bad Request` - Invalid ID format
- `500 Internal Server Error` - Server error

---

#### 3. Create New Book
```http
POST /api/v1/Books
```

**Description:** Creates a new book in the system.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "The Great Gatsby",
  "description": "The story of the mysteriously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.",
  "pageCount": 180,
  "excerpt": "In my younger and more vulnerable years my father gave me some advice that I've carried with me ever since.",
  "publishDate": "1925-04-10T00:00:00.000Z"
}
```

**Example Response:**
```json
{
  "id": 201,
  "title": "The Great Gatsby",
  "description": "The story of the mysteriously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.",
  "pageCount": 180,
  "excerpt": "In my younger and more vulnerable years my father gave me some advice that I've carried with me ever since.",
  "publishDate": "1925-04-10T00:00:00.000Z"
}
```

**Status Codes:**
- `200 OK` or `201 Created` - Book successfully created
- `400 Bad Request` - Invalid request data or missing required fields
- `500 Internal Server Error` - Server error

**Validation Rules:**
- `title` must not be empty
- `pageCount` must be a positive integer
- `publishDate` must be in valid ISO 8601 format (if provided)

---

#### 4. Update Book
```http
PUT /api/v1/Books/{id}
```

**Description:** Updates an existing book with new information.

**Parameters:**
| Parameter | Type | Location | Required | Description |
|-----------|------|----------|----------|-------------|
| `id` | integer | path | Yes | Unique book identifier |

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "id": 1,
  "title": "Updated Book Title",
  "description": "Updated description of the book.",
  "pageCount": 350,
  "excerpt": "Updated excerpt text.",
  "publishDate": "2024-01-15T00:00:00.000Z"
}
```

**Example Response:**
```json
{
  "id": 1,
  "title": "Updated Book Title",
  "description": "Updated description of the book.",
  "pageCount": 350,
  "excerpt": "Updated excerpt text.",
  "publishDate": "2024-01-15T00:00:00.000Z"
}
```

**Status Codes:**
- `200 OK` - Book successfully updated
- `400 Bad Request` - Invalid request data or ID mismatch
- `404 Not Found` - Book with specified ID not found
- `500 Internal Server Error` - Server error

---

#### 5. Delete Book
```http
DELETE /api/v1/Books/{id}
```

**Description:** Deletes a book from the system.

**Parameters:**
| Parameter | Type | Location | Required | Description |
|-----------|------|----------|----------|-------------|
| `id` | integer | path | Yes | Unique book identifier |

**Example Request:**
```http
DELETE /api/v1/Books/1
```

**Example Response:**
```json
{}
```

**Status Codes:**
- `200 OK` - Book successfully deleted
- `404 Not Found` - Book with specified ID not found
- `400 Bad Request` - Invalid ID format
- `500 Internal Server Error` - Server error

---

## 👥 Authors API

### Base Endpoint
```
/api/v1/Authors
```

### Data Model

#### Author Object
```json
{
  "id": 1,
  "idBook": 1,
  "firstName": "string",
  "lastName": "string"
}
```

#### Field Descriptions
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | integer | No (auto-generated) | Unique identifier for the author |
| `idBook` | integer | No | Associated book ID (foreign key) |
| `firstName` | string | Yes | Author's first name |
| `lastName` | string | Yes | Author's last name |

### Endpoints

#### 1. Get All Authors
```http
GET /api/v1/Authors
```

**Description:** Retrieves a list of all authors in the system.

**Example Response:**
```json
[
  {
    "id": 1,
    "idBook": 1,
    "firstName": "Harper",
    "lastName": "Lee"
  },
  {
    "id": 2,
    "idBook": 2,
    "firstName": "George",
    "lastName": "Orwell"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `500 Internal Server Error` - Server error

---

#### 2. Get Author by ID
```http
GET /api/v1/Authors/{id}
```

**Description:** Retrieves a specific author by their unique identifier.

**Parameters:**
| Parameter | Type | Location | Required | Description |
|-----------|------|----------|----------|-------------|
| `id` | integer | path | Yes | Unique author identifier |

**Example Response:**
```json
{
  "id": 1,
  "idBook": 1,
  "firstName": "Harper",
  "lastName": "Lee"
}
```

**Status Codes:**
- `200 OK` - Author found and returned
- `404 Not Found` - Author with specified ID not found
- `400 Bad Request` - Invalid ID format
- `500 Internal Server Error` - Server error

---

#### 3. Create New Author
```http
POST /api/v1/Authors
```

**Description:** Creates a new author in the system.

**Request Body:**
```json
{
  "idBook": 1,
  "firstName": "F. Scott",
  "lastName": "Fitzgerald"
}
```

**Example Response:**
```json
{
  "id": 101,
  "idBook": 1,
  "firstName": "F. Scott",
  "lastName": "Fitzgerald"
}
```

**Status Codes:**
- `200 OK` or `201 Created` - Author successfully created
- `400 Bad Request` - Invalid request data or missing required fields
- `500 Internal Server Error` - Server error

**Validation Rules:**
- `firstName` must not be empty
- `lastName` must not be empty
- `idBook` must be a valid integer (if provided)

---

#### 4. Update Author
```http
PUT /api/v1/Authors/{id}
```

**Description:** Updates an existing author with new information.

**Request Body:**
```json
{
  "id": 1,
  "idBook": 1,
  "firstName": "Harper",
  "lastName": "Lee"
}
```

**Status Codes:**
- `200 OK` - Author successfully updated
- `400 Bad Request` - Invalid request data or ID mismatch
- `404 Not Found` - Author with specified ID not found
- `500 Internal Server Error` - Server error

---

#### 5. Delete Author
```http
DELETE /api/v1/Authors/{id}
```

**Description:** Deletes an author from the system.

**Status Codes:**
- `200 OK` - Author successfully deleted
- `404 Not Found` - Author with specified ID not found
- `400 Bad Request` - Invalid ID format
- `500 Internal Server Error` - Server error

---

## 🔗 Relationships

### Book-Author Relationship
- An author can be associated with a book using the `idBook` field
- Multiple authors can be associated with the same book
- Authors can exist without being associated with any book (`idBook` can be null)

**Example:** Get all authors for a specific book:
```javascript
// First, get all authors
GET /api/v1/Authors

// Then filter by idBook in your application logic
const authorsForBook1 = authors.filter(author => author.idBook === 1);
```

---

## 📋 HTTP Status Codes

| Status Code | Description | Usage |
|-------------|-------------|-------|
| `200 OK` | Success | Successful GET, PUT, DELETE operations |
| `201 Created` | Created | Successful POST operations (resource creation) |
| `400 Bad Request` | Client Error | Invalid request format, missing required fields, validation errors |
| `404 Not Found` | Not Found | Requested resource does not exist |
| `500 Internal Server Error` | Server Error | Unexpected server errors |

---

## 🚨 Error Responses

### Error Format
```json
{
  "type": "string",
  "title": "string",
  "status": 400,
  "detail": "string",
  "instance": "string",
  "errors": {},
  "traceId": "string"
}
```

### Common Error Examples

#### 400 Bad Request
```json
{
  "type": "https://tools.ietf.org/html/rfc7231#section-6.5.1",
  "title": "One or more validation errors occurred.",
  "status": 400,
  "detail": "The input was not valid.",
  "instance": "/api/v1/Books",
  "errors": {
    "title": ["The title field is required."],
    "pageCount": ["The page count must be a positive number."]
  },
  "traceId": "00-8a1b2c3d4e5f6789-0123456789abcdef-00"
}
```

#### 404 Not Found
```json
{
  "type": "https://tools.ietf.org/html/rfc7231#section-6.5.4",
  "title": "Not Found",
  "status": 404,
  "detail": "Book with ID 999 was not found.",
  "instance": "/api/v1/Books/999",
  "traceId": "00-8a1b2c3d4e5f6789-0123456789abcdef-00"
}
```

---

## 🔄 Best Practices

### Request Guidelines
1. **Content-Type**: Always include `Content-Type: application/json` for POST and PUT requests
2. **ID Consistency**: When updating resources, ensure the ID in the URL matches the ID in the request body
3. **Date Format**: Use ISO 8601 format for dates: `YYYY-MM-DDTHH:mm:ss.sssZ`
4. **Field Validation**: Validate required fields before sending requests

### Response Handling
1. **Status Codes**: Always check HTTP status codes before processing response data
2. **Error Handling**: Implement proper error handling for 4xx and 5xx status codes
3. **Null Values**: Handle potential null values in optional fields
4. **Pagination**: For large datasets, implement client-side filtering as needed

---

## 📝 Example Use Cases

### 1. Create a Complete Book Entry
```bash
# Step 1: Create a book
curl -X POST "https://fakerestapi.azurewebsites.net/api/v1/Books" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Catcher in the Rye",
    "description": "A controversial novel about teenage rebellion and alienation.",
    "pageCount": 277,
    "excerpt": "If you really want to hear about it, the first thing you'll probably want to know is where I was born.",
    "publishDate": "1951-07-16T00:00:00.000Z"
  }'

# Step 2: Create an author for the book (using returned book ID)
curl -X POST "https://fakerestapi.azurewebsites.net/api/v1/Authors" \
  -H "Content-Type: application/json" \
  -d '{
    "idBook": 201,
    "firstName": "J.D.",
    "lastName": "Salinger"
  }'
```

### 2. Search and Update Workflow
```bash
# Step 1: Get all books
curl -X GET "https://fakerestapi.azurewebsites.net/api/v1/Books"

# Step 2: Get specific book
curl -X GET "https://fakerestapi.azurewebsites.net/api/v1/Books/1"

# Step 3: Update the book
curl -X PUT "https://fakerestapi.azurewebsites.net/api/v1/Books/1" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "Updated Title",
    "description": "Updated description",
    "pageCount": 350,
    "excerpt": "Updated excerpt",
    "publishDate": "2024-01-01T00:00:00.000Z"
  }'
```

### 3. Data Cleanup
```bash
# Delete an author
curl -X DELETE "https://fakerestapi.azurewebsites.net/api/v1/Authors/1"

# Delete a book
curl -X DELETE "https://fakerestapi.azurewebsites.net/api/v1/Books/1"
```

---

## 🧪 Testing Guidelines

### Automated Testing Considerations
1. **Test Data**: Use unique titles and names to avoid conflicts
2. **Cleanup**: Always clean up test data after test execution
3. **Parallel Testing**: Be aware that parallel tests may interfere with each other
4. **Rate Limiting**: Implement delays between requests if needed
5. **Idempotency**: DELETE operations should be idempotent

### Performance Testing
- **Load Testing**: The API can handle moderate concurrent requests
- **Response Time**: Typical response times range from 100-2000ms
- **Throughput**: Recommended maximum of 10 requests per second per endpoint

---

## 🔧 Development Tools

### Recommended Tools
- **Postman**: For manual API testing and collection management
- **curl**: For command-line testing and scripting
- **REST Assured**: For Java-based automated testing
- **Newman**: For automated Postman collection runs
- **Insomnia**: Alternative GUI tool for API testing

### Code Examples

#### Java (REST Assured)
```java
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

// Get all books
given()
    .contentType("application/json")
.when()
    .get("https://fakerestapi.azurewebsites.net/api/v1/Books")
.then()
    .statusCode(200)
    .body("size()", greaterThan(0));
```

#### Python (requests)
```python
import requests

# Create a new book
book_data = {
    "title": "Sample Book",
    "description": "A sample book for testing",
    "pageCount": 200,
    "excerpt": "Sample excerpt",
    "publishDate": "2024-01-01T00:00:00.000Z"
}

response = requests.post(
    "https://fakerestapi.azurewebsites.net/api/v1/Books",
    json=book_data,
    headers={"Content-Type": "application/json"}
)

print(f"Status: {response.status_code}")
print(f"Response: {response.json()}")
```

#### JavaScript (fetch)
```javascript
// Get book by ID
async function getBook(id) {
    try {
        const response = await fetch(`https://fakerestapi.azurewebsites.net/api/v1/Books/${id}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const book = await response.json();
        console.log('Book:', book);
        return book;
    } catch (error) {
        console.error('Error fetching book:', error);
    }
}
```

---

## 📞 Support

### Resources
- **API Base URL**: https://fakerestapi.azurewebsites.net
- **Swagger Documentation**: https://fakerestapi.azurewebsites.net/swagger
- **GitHub Repository**: Link to your automation framework repository
- **Issue Tracking**: GitHub Issues for bug reports and feature requests

### Contact Information
- **Development Team**: your-team@company.com
- **Support**: api-support@company.com
- **Documentation**: docs@company.com

---

## 📜 Changelog

### Version 1.0.0 (Current)
- Initial API documentation
- Complete CRUD operations for Books and Authors
- Error handling and status codes
- Example requests and responses
- Testing guidelines

### Planned Features
- Authentication and authorization
- Pagination for large datasets
- Advanced search and filtering
- Bulk operations
- Webhooks for real-time updates

---

*This documentation is maintained by the API Development Team and is updated regularly to reflect the current state of the Bookstore API.*
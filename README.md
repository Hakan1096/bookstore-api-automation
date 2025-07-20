# Complete Bookstore API Automation Framework

## 📁 Complete Project Directory Structure

```
bookstore-api-automation/
├── .github/
│   └── workflows/
│       └── ci-cd-pipeline.yml
├── .docker/
├── .git/
├── .gitignore
├── .dockerignore
├── README.md
├── CONTRIBUTING.md
├── LICENSE
├── pom.xml
├── testng.xml
├── allure.properties
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── docker-compose.staging.yml
│   ├── docker-compose.prod.yml
│   ├── entrypoint.sh
│   └── nginx.conf
├── scripts/
│   ├── run-tests.sh
│   ├── generate-report.sh
│   ├── docker-run.sh
│   └── setup-environment.sh
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bookstore/
│   │   │           ├── config/
│   │   │           │   ├── ApiConfig.java
│   │   │           │   └── TestConfig.java
│   │   │           ├── models/
│   │   │           │   ├── Book.java
│   │   │           │   ├── Author.java
│   │   │           │   ├── ApiResponse.java
│   │   │           │   └── ErrorResponse.java
│   │   │           ├── clients/
│   │   │           │   ├── BaseApiClient.java
│   │   │           │   ├── BookApiClient.java
│   │   │           │   └── AuthorApiClient.java
│   │   │           └── utils/
│   │   │               ├── TestDataGenerator.java
│   │   │               ├── JsonUtils.java
│   │   │               ├── FileUtils.java
│   │   │               ├── PerformanceUtils.java
│   │   │               ├── ResponseValidator.java
│   │   │               ├── RetryUtils.java
│   │   │               ├── WaitUtils.java
│   │   │               ├── EnvironmentUtils.java
│   │   │               ├── TestGroupConstants.java
│   │   │               └── ApiResponseMatcher.java
│   │   └── resources/
│   │       ├── config.properties
│   │       ├── logback.xml
│   │       └── test-data/
│   │           ├── valid-book.json
│   │           ├── invalid-book.json
│   │           ├── book-with-special-chars.json
│   │           ├── book-minimal.json
│   │           ├── book-large-fields.json
│   │           ├── valid-author.json
│   │           ├── invalid-author.json
│   │           ├── author-with-special-chars.json
│   │           ├── author-minimal.json
│   │           ├── author-long-names.json
│   │           ├── bulk-books.json
│   │           └── bulk-authors.json
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── bookstore/
│       │           ├── base/
│       │           │   └── BaseTest.java
│       │           └── tests/
│       │               ├── books/
│       │               │   ├── BookApiHappyPathTests.java
│       │               │   ├── BookApiEdgeCaseTests.java
│       │               │   └── BookApiNegativeTests.java
│       │               ├── authors/
│       │               │   ├── AuthorApiHappyPathTests.java
│       │               │   ├── AuthorApiEdgeCaseTests.java
│       │               │   └── AuthorApiNegativeTests.java
│       │               ├── integration/
│       │               │   └── BookAuthorIntegrationTests.java
│       │               ├── performance/
│       │               │   ├── BookApiPerformanceTests.java
│       │               │   └── AuthorApiPerformanceTests.java
│       │               └── examples/
│       │                   └── ComprehensiveWorkflowTests.java
│       └── resources/
│           ├── smoke-tests.xml
│           ├── regression-tests.xml
│           ├── performance-tests.xml
│           └── categories.json
├── target/
│   ├── classes/
│   ├── test-classes/
│   ├── allure-results/
│   ├── allure-report/
│   ├── surefire-reports/
│   └── logs/
│       ├── application.log
│       ├── tests.log
│       └── http.log
└── docs/
    ├── API_DOCUMENTATION.md
    ├── TESTING_STRATEGY.md
    ├── PERFORMANCE_BENCHMARKS.md
    └── TROUBLESHOOTING.md
```

## 🚀 Quick Start Guide

### Prerequisites Installation

```bash
# Install Java 11+
sudo apt-get update
sudo apt-get install openjdk-11-jdk

# Install Maven
sudo apt-get install maven

# Install Docker (optional)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Verify installations
java -version
mvn -version
docker --version
```

### Repository Setup

```bash
# Clone the repository
git clone https://github.com/your-org/bookstore-api-automation.git
cd bookstore-api-automation

# Make scripts executable
chmod +x scripts/*.sh
chmod +x docker/entrypoint.sh

# Install dependencies
mvn clean install -DskipTests

# Verify setup
mvn compile test-compile
```

## 🧪 Running Tests

### Local Execution

```bash
# Run all tests with default configuration
mvn clean test

# Run specific test suite
mvn clean test -DsuiteXmlFile=src/test/resources/smoke-tests.xml

# Run with specific profile
mvn clean test -Pstaging

# Run with custom parallel threads
mvn clean test -DthreadCount=3

# Run specific test class
mvn clean test -Dtest=BookApiHappyPathTests

# Run specific test method
mvn clean test -Dtest=BookApiHappyPathTests#testGetAllBooks

# Run with custom API URL
mvn clean test -Dapi.base.url=https://your-api.com

# Generate and serve Allure report
mvn allure:serve
```

### Using Shell Scripts

```bash
# Run tests with script (recommended)
./scripts/run-tests.sh

# Run with specific environment
./scripts/run-tests.sh -e staging

# Run smoke tests only
./scripts/run-tests.sh -s smoke-tests.xml

# Run with custom thread count and generate report
./scripts/run-tests.sh -t 2 -r

# Generate report only
./scripts/generate-report.sh
```

### Docker Execution

```bash
# Build Docker image
docker build -t bookstore-api-tests .

# Run tests in Docker
docker run --rm bookstore-api-tests

# Run with custom environment variables
docker run --rm \
  -e API_BASE_URL=https://staging-api.com \
  -e PARALLEL_THREADS=2 \
  bookstore-api-tests

# Run specific test suite
docker run --rm \
  -e TEST_SUITE=src/test/resources/smoke-tests.xml \
  bookstore-api-tests smoke-tests

# Run with volume for reports
docker run --rm \
  -v $(pwd)/target:/app/target \
  bookstore-api-tests

# Use docker-compose for full setup
docker-compose up --build

# Run against staging environment
docker-compose -f docker-compose.yml -f docker-compose.staging.yml up
```

### Using Helper Scripts

```bash
# Run Docker tests with script
./scripts/docker-run.sh

# Run with specific configuration
./scripts/docker-run.sh -u http://staging-api.com -t 2 smoke-tests

# Serve reports after Docker execution
./scripts/docker-run.sh serve-reports
```

## 📊 Test Categories and Groups

### Available Test Groups

```bash
# Smoke tests (essential functionality)
mvn test -Dgroups=smoke

# Regression tests (comprehensive coverage)
mvn test -Dgroups=regression

# Performance tests
mvn test -Dgroups=performance

# Integration tests
mvn test -Dgroups=integration

# Books API only
mvn test -Dgroups=books

# Authors API only
mvn test -Dgroups=authors

# Happy path tests only
mvn test -Dgroups=happy_path

# Negative tests only
mvn test -Dgroups=negative

# Edge case tests only
mvn test -Dgroups=edge_cases
```

### Test Execution Examples

```bash
# Run critical tests only
mvn test -Dgroups=critical

# Run books API smoke tests
mvn test -Dgroups="books,smoke"

# Exclude performance tests
mvn test -DexcludedGroups=performance

# Run integration and regression tests
mvn test -Dgroups="integration,regression"
```

## 📈 Performance Testing

### Load Testing Examples

```bash
# Run performance tests
mvn test -Dtest=*PerformanceTests

# Run with specific performance configuration
mvn test -Dtest=BookApiPerformanceTests \
  -Dload.test.requests=100 \
  -Dload.test.concurrency=10

# Performance test with Docker
docker run --rm \
  -e TEST_SUITE=src/test/resources/performance-tests.xml \
  bookstore-api-tests
```

### Custom Performance Test Configuration

```java
// Example usage in test
@Test
public void customPerformanceTest() {
    PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
        () -> bookApiClient.getAllBooks().isSuccess(),
        50,    // 50 requests
        5      // 5 concurrent users
    );
    
    Assert.assertTrue(result.getSuccessRate() >= 95.0);
    Assert.assertTrue(result.getAverageResponseTime() < 2000);
}
```

## 🔧 Configuration Management

### Environment Variables

```bash
# API Configuration
export API_BASE_URL=https://fakerestapi.azurewebsites.net
export API_TIMEOUT=30000
export API_RETRY_COUNT=3

# Test Configuration
export PARALLEL_THREADS=5
export TEST_SUITE=testng.xml
export DATA_CLEANUP=true
export GENERATE_REPORT=true

# Logging Configuration
export LOG_LEVEL=INFO
export LOG_REQUESTS=true
export LOG_RESPONSES=true

# Environment
export ENVIRONMENT=staging
```

### Maven Profiles

```bash
# Development environment (default)
mvn test -Pdev

# Staging environment
mvn test -Pstaging

# Production environment
mvn test -Pprod

# Smoke test profile
mvn test -Psmoke

# Regression test profile
mvn test -Pregression

# Docker profile
mvn test -Pdocker
```

## 📋 Test Data Management

### Using File-Based Test Data

```java
// Load predefined test data
Book validBook = FileUtils.getValidBook();
Author validAuthor = FileUtils.getValidAuthor();

// Load bulk test data
List<Book> bulkBooks = FileUtils.getBulkBooks();
List<Author> bulkAuthors = FileUtils.getBulkAuthors();

// Load custom test data
Book customBook = FileUtils.loadBookFromFile("custom-book.json");
```

### Using Generated Test Data

```java
// Generate random valid data
Book randomBook = TestDataGenerator.generateValidBook();
Author randomAuthor = TestDataGenerator.generateValidAuthor();

// Generate specific test scenarios
Book longFieldBook = TestDataGenerator.generateBookWithLongFields();
Book specialCharBook = TestDataGenerator.generateBookWithSpecialCharacters();
Book invalidBook = TestDataGenerator.generateInvalidBook();
```

## 🔍 Debugging and Troubleshooting

### Enable Debug Mode

```bash
# Enable debug logging
mvn test -Dlog.level=DEBUG

# Enable verbose TestNG output
mvn test -Dverbose=true

# Enable request/response logging
mvn test -Dapi.log.requests=true -Dapi.log.responses=true

# Run single test with debug
mvn test -Dtest=BookApiHappyPathTests#testGetAllBooks -Ddebug=true
```

### Common Issues and Solutions

1. **Connection Timeouts**
   ```bash
   # Increase timeout
   mvn test -Dapi.timeout=60000
   ```

2. **Parallel Execution Issues**
   ```bash
   # Reduce thread count
   mvn test -DthreadCount=1
   ```

3. **Memory Issues**
   ```bash
   # Increase memory
   export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
   ```

4. **Docker Issues**
   ```bash
   # Check logs
   docker logs bookstore-api-automation
   
   # Debug container
   docker run -it --entrypoint=/bin/bash bookstore-api-tests
   ```

## 📊 Reporting and CI/CD

### Allure Reporting

```bash
# Generate report
mvn allure:report

# Serve report locally
mvn allure:serve

# Report with history
mvn allure:report -Dallure.results.directory=target/allure-results

# Custom report configuration
mvn allure:report -Dallure.report.name="Custom API Test Report"
```

### CI/CD Integration

```bash
# GitHub Actions (automatic on push/PR)
git push origin main

# Manual workflow trigger
gh workflow run "Bookstore API Test Automation" \
  --field test_suite=smoke \
  --field environment=staging \
  --field parallel_threads=3

# Jenkins (if using Jenkins)
curl -X POST "http://jenkins-url/job/bookstore-api-tests/build"
```

## 🛠 Framework Extension Examples

### Adding New Test Classes

```java
@Feature("New API Feature Tests")
public class NewFeatureTests extends BaseTest {
    
    @Test(groups = {TestGroupConstants.SMOKE, "new_feature"})
    @Description("Test new feature functionality")
    public void testNewFeature() {
        // Your test implementation
    }
}
```

### Adding New API Clients

```java
public class NewApiClient extends BaseApiClient {
    
    @Step("Call new API endpoint")
    public ApiResponse<NewModel> callNewEndpoint() {
        return executeGet(config.getNewEndpoint(), NewModel.class);
    }
}
```

### Custom Test Data

```json
// src/main/resources/test-data/custom-data.json
{
  "customField": "customValue",
  "nested": {
    "property": "value"
  }
}
```

## 📚 Best Practices

### Test Organization
- Group related tests in the same class
- Use descriptive test names
- Add proper Allure annotations
- Follow naming conventions

### Data Management
- Use TestDataGenerator for dynamic data
- Use FileUtils for static test data
- Clean up test data after execution
- Avoid hardcoded values

### Error Handling
- Use RetryUtils for flaky operations
- Implement proper assertion messages
- Handle expected failures gracefully
- Log meaningful information

### Performance
- Use appropriate thread counts
- Monitor response times
- Set reasonable timeouts
- Use parallel execution judiciously

## 🎯 Framework Benefits

### Comprehensive Coverage
- ✅ Happy path scenarios
- ✅ Edge cases and boundary conditions
- ✅ Negative testing and error handling
- ✅ Integration testing
- ✅ Performance testing

### Maintainability
- ✅ Clean architecture with SOLID principles
- ✅ Reusable components and utilities
- ✅ Configuration-driven approach
- ✅ Comprehensive documentation

### Scalability
- ✅ Parallel test execution
- ✅ Docker containerization
- ✅ CI/CD pipeline integration
- ✅ Environment-specific configurations

### Reporting
- ✅ Detailed Allure reports
- ✅ Performance metrics
- ✅ Test execution trends
- ✅ Failure analysis

This comprehensive framework provides everything needed for robust API automation testing, from basic functionality validation to performance testing and CI/CD integration. The modular design allows for easy extension and maintenance while providing excellent reporting and debugging capabilities.
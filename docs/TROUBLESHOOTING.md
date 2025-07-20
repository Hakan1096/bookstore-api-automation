# Troubleshooting Guide - Bookstore API Automation Framework

## 🔧 Overview

This comprehensive troubleshooting guide provides solutions for common issues encountered while working with the Bookstore API automation framework. Issues are categorized by area and include step-by-step resolution procedures.

**Framework Version:** 1.0.0  
**Last Updated:** July 2025  
**Scope:** Setup, Execution, Performance, CI/CD, and Deployment Issues

---

## 📋 Quick Issue Index

| Issue Category | Common Problems | Quick Links |
|----------------|-----------------|-------------|
| **🏗 Setup & Configuration** | Maven, Java, Dependencies | [Jump to Setup Issues](#-setup--configuration-issues) |
| **🧪 Test Execution** | Test failures, Flaky tests | [Jump to Test Issues](#-test-execution-issues) |
| **⚡ Performance Testing** | Load tests, Timeouts | [Jump to Performance Issues](#-performance-testing-issues) |
| **🌐 Network & API** | Connectivity, Timeouts | [Jump to Network Issues](#-network--api-connectivity-issues) |
| **🐳 Docker & Containers** | Build failures, Runtime errors | [Jump to Docker Issues](#-docker--containerization-issues) |
| **🚀 CI/CD Pipeline** | GitHub Actions, Build failures | [Jump to CI/CD Issues](#-cicd--pipeline-issues) |
| **📊 Reporting & Allure** | Report generation, Display issues | [Jump to Reporting Issues](#-reporting--allure-issues) |
| **💾 Data & Environment** | Test data, Environment setup | [Jump to Data Issues](#-data--environment-issues) |

---

## 🏗 Setup & Configuration Issues

### Issue 1: Compilation Error - `timeout()` Method Not Found

**Problem:**
```bash
[ERROR] cannot find symbol
[ERROR]   symbol:   method timeout(int,java.util.concurrent.TimeUnit)
[ERROR]   location: interface io.restassured.specification.RequestSpecification
```

**Root Cause:** REST Assured version compatibility issue with the `timeout()` method.

**Solution:**

**Option A: Remove timeout configuration (Quick Fix)**
```java
// Edit src/main/java/com/bookstore/clients/BaseApiClient.java
protected RequestSpecification getBaseRequestSpec() {
    return RestAssured.given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
            // Remove the .timeout() line completely
}
```

**Option B: Use config-based timeout (Recommended)**
```java
// Replace the timeout line with:
protected RequestSpecification getBaseRequestSpec() {
    return RestAssured.given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .config(RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, config.getTimeout())
                    .setParam(CoreConnectionPNames.SO_TIMEOUT, config.getTimeout())));
}
```

**Verification:**
```bash
mvn clean compile
# Should compile without errors
```

---

### Issue 2: Maven Dependency Resolution Failures

**Problem:**
```bash
[ERROR] Failed to execute goal on project bookstore-api-automation: 
Could not resolve dependencies for project com.bookstore:bookstore-api-automation:jar:1.0.0
```

**Root Cause:** Network issues, repository configuration, or dependency conflicts.

**Diagnosis Steps:**
```bash
# Check Maven configuration
mvn help:effective-settings

# Check dependency tree
mvn dependency:tree

# Clear Maven cache
rm -rf ~/.m2/repository
mvn clean install
```

**Solutions:**

**Solution A: Network/Proxy Issues**
```bash
# Configure Maven proxy (if behind corporate firewall)
nano ~/.m2/settings.xml
```
```xml
<settings>
  <proxies>
    <proxy>
      <id>corporate-proxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>proxy.company.com</host>
      <port>8080</port>
    </proxy>
  </proxies>
</settings>
```

**Solution B: Repository Configuration**
```bash
# Add to pom.xml if needed
<repositories>
    <repository>
        <id>maven-central</id>
        <url>https://repo1.maven.org/maven2</url>
    </repository>
</repositories>
```

**Solution C: Force Dependency Update**
```bash
mvn clean install -U  # Force update snapshots
mvn dependency:purge-local-repository  # Clean local cache
```

---

### Issue 3: Java Version Compatibility

**Problem:**
```bash
[ERROR] Source option 11 is no longer supported. Use 11 or later.
```

**Root Cause:** Incorrect Java version installed or configured.

**Diagnosis:**
```bash
# Check Java version
java -version
javac -version

# Check JAVA_HOME
echo $JAVA_HOME

# Check Maven Java version
mvn -version
```

**Solution:**
```bash
# Install Java 11+ (macOS with Homebrew)
brew install openjdk@11

# Set JAVA_HOME (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Ubuntu/Debian
sudo apt install openjdk-11-jdk
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Reload shell configuration
source ~/.zshrc  # or ~/.bash_profile
```

---

### Issue 4: Lombok Compilation Issues

**Problem:**
```bash
[ERROR] cannot find symbol: method builder()
[ERROR] cannot find symbol: variable log
```

**Root Cause:** Lombok not properly configured in IDE or Maven.

**Solution:**

**For IntelliJ IDEA:**
```bash
# 1. Install Lombok plugin
# File → Settings → Plugins → Search "Lombok" → Install

# 2. Enable annotation processing
# File → Settings → Build → Compiler → Annotation Processors
# Check "Enable annotation processing"
```

**For Eclipse:**
```bash
# Download lombok.jar and run:
java -jar lombok.jar
# Follow installation wizard
```

**For Maven (already in pom.xml):**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

**Verification:**
```bash
mvn clean compile
# Check that @Data, @Builder annotations work
```

---

## 🧪 Test Execution Issues

### Issue 5: Test Failures Due to API Unavailability

**Problem:**
```bash
[ERROR] testGetAllBooks: java.net.ConnectException: Connection refused
```

**Root Cause:** Target API service is down or unreachable.

**Diagnosis:**
```bash
# Check API availability manually
curl -I https://fakerestapi.azurewebsites.net/api/v1/Books

# Check network connectivity
ping fakerestapi.azurewebsites.net

# Check DNS resolution
nslookup fakerestapi.azurewebsites.net
```

**Solutions:**

**Solution A: Wait and Retry**
```bash
# Retry after a few minutes
mvn test -Dtest=BookApiHappyPathTests#testGetAllBooks

# Check service status
curl -v https://fakerestapi.azurewebsites.net/health  # if health endpoint exists
```

**Solution B: Use Alternative Environment**
```bash
# Switch to different environment profile
mvn test -Pstaging  # if staging environment available

# Or run with different base URL
mvn test -Dapi.base.url=https://backup-api-url.com
```

**Solution C: Implement Health Check**
```java
// Add to BaseTest.java
@BeforeSuite
public void verifyApiConnectivity() {
    try {
        RestAssured.given()
            .get(apiConfig.getBaseUrl() + "/api/v1/Books")
            .then()
            .statusCode(anyOf(is(200), is(404))); // Accept 200 or 404, just need connectivity
        
        log.info("✅ API connectivity verified");
    } catch (Exception e) {
        throw new SkipException("❌ API is unreachable: " + e.getMessage());
    }
}
```

---

### Issue 6: Flaky Test Failures

**Problem:**
```bash
Test 'testCreateBookWithValidData' passed on first run but failed on second run
```

**Root Cause:** Test dependencies, timing issues, or data conflicts.

**Diagnosis Steps:**
```bash
# Run the same test multiple times
mvn test -Dtest=BookApiHappyPathTests#testCreateBookWithValidData

# Run with different thread counts
mvn test -DthreadCount=1  # Sequential execution
mvn test -DthreadCount=5  # Parallel execution

# Check for data dependencies
grep -r "testCreateBookWithValidData" src/test/
```

**Solutions:**

**Solution A: Add Data Isolation**
```java
@BeforeMethod
public void setupUniqueTestData() {
    // Generate unique data for each test run
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    testBook = TestDataGenerator.generateValidBook();
    testBook.setTitle(testBook.getTitle() + "_" + uniqueId);
}
```

**Solution B: Add Retry Logic**
```java
// Add retry analyzer to flaky tests
@Test(retryAnalyzer = FlakyTestRetryAnalyzer.class)
public void testCreateBookWithValidData() {
    // Test implementation
}

// Implement retry analyzer
public class FlakyTestRetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static int maxTry = 3;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && count < maxTry) {
            count++;
            return true;
        }
        return false;
    }
}
```

**Solution C: Add Wait Conditions**
```java
// Replace hard waits with intelligent waits
public void waitForBookCreation(int bookId) {
    await().atMost(10, SECONDS)
           .pollInterval(1, SECONDS)
           .until(() -> {
               ApiResponse<Book> response = bookApiClient.getBookById(bookId);
               return response.isSuccess();
           });
}
```

---

### Issue 7: Parallel Test Execution Failures

**Problem:**
```bash
[ERROR] Tests run: 25, Failures: 8, Errors: 2, Skipped: 0
# But same tests pass when run sequentially
```

**Root Cause:** Shared resources, race conditions, or insufficient resources.

**Diagnosis:**
```bash
# Compare sequential vs parallel execution
mvn test -DthreadCount=1  # Sequential
mvn test -DthreadCount=5  # Parallel

# Check resource usage during tests
top -p $(pgrep -f maven)  # Monitor during test execution
```

**Solutions:**

**Solution A: Reduce Thread Count**
```bash
# Reduce parallel threads
mvn test -DthreadCount=2

# Or modify testng.xml
<suite thread-count="2">
```

**Solution B: Fix Resource Conflicts**
```java
// Use thread-safe data generation
public class ThreadSafeTestDataGenerator {
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    public static Book generateUniqueBook() {
        int uniqueId = counter.incrementAndGet();
        return Book.builder()
            .title("Test Book " + uniqueId + "_" + Thread.currentThread().getId())
            .build();
    }
}
```

**Solution C: Configure Resource Limits**
```xml
<!-- In pom.xml surefire plugin -->
<configuration>
    <forkCount>1</forkCount>
    <reuseForks>true</reuseForks>
    <argLine>-Xmx1024m -XX:MaxPermSize=256m</argLine>
</configuration>
```

---

## ⚡ Performance Testing Issues

### Issue 8: Performance Tests Timing Out

**Problem:**
```bash
[ERROR] testConcurrentGetAllBooks: java.util.concurrent.TimeoutException
```

**Root Cause:** Insufficient timeout values or poor API performance.

**Diagnosis:**
```bash
# Test API manually for baseline
time curl https://fakerestapi.azurewebsites.net/api/v1/Books

# Check current timeout settings
grep -r "timeout" src/main/resources/config.properties
```

**Solutions:**

**Solution A: Increase Timeouts**
```properties
# Update config.properties
api.timeout=60000  # Increase to 60 seconds
api.retry.count=5  # Increase retry attempts
```

**Solution B: Adjust Performance Test Parameters**
```java
// Reduce load for unstable environments
PerformanceUtils.PerformanceResult result = PerformanceUtils.executeLoadTest(
    () -> {
        ApiResponse<List<Book>> response = bookApiClient.getAllBooks();
        return response.isSuccess();
    },
    25,  // Reduced from 50 requests
    5    // Reduced from 10 concurrent users
);
```

**Solution C: Add Progressive Loading**
```java
// Implement gradual load increase
public void testProgressiveLoad() {
    // Start with light load
    runLoadTest(10, 2);  // 10 requests, 2 users
    Thread.sleep(30000); // Wait between tests
    
    // Increase gradually
    runLoadTest(25, 5);  // 25 requests, 5 users
    Thread.sleep(30000);
    
    runLoadTest(50, 10); // Full load
}
```

---

### Issue 9: Memory Issues During Performance Tests

**Problem:**
```bash
[ERROR] java.lang.OutOfMemoryError: Java heap space
```

**Root Cause:** Insufficient memory allocation or memory leaks.

**Diagnosis:**
```bash
# Check current memory usage
jps -v | grep maven  # Check JVM arguments

# Monitor memory during test execution
jstat -gc -t [PID] 5s  # Monitor garbage collection
```

**Solutions:**

**Solution A: Increase Memory Allocation**
```bash
# Set Maven memory options
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Or modify pom.xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-Xmx2048m -XX:MaxPermSize=512m</argLine>
    </configuration>
</plugin>
```

**Solution B: Optimize Test Data Management**
```java
// Clear large objects after use
@AfterMethod
public void cleanupMemory() {
    // Clear any large data structures
    testResults = null;
    largeResponseData = null;
    
    // Suggest garbage collection
    System.gc();
}
```

**Solution C: Use Streaming for Large Responses**
```java
// Process large responses in chunks
public void processLargeResponse(ApiResponse<List<Book>> response) {
    List<Book> books = response.getData();
    
    // Process in batches to reduce memory footprint
    int batchSize = 100;
    for (int i = 0; i < books.size(); i += batchSize) {
        List<Book> batch = books.subList(i, Math.min(i + batchSize, books.size()));
        processBatch(batch);
        batch = null; // Help GC
    }
}
```

---

## 🌐 Network & API Connectivity Issues

### Issue 10: Intermittent Connection Timeouts

**Problem:**
```bash
[ERROR] java.net.SocketTimeoutException: Read timed out
```

**Root Cause:** Network instability or API response delays.

**Diagnosis:**
```bash
# Test network stability
ping -c 10 fakerestapi.azurewebsites.net

# Check response times over time
for i in {1..10}; do
    time curl -s https://fakerestapi.azurewebsites.net/api/v1/Books > /dev/null
    sleep 5
done
```

**Solutions:**

**Solution A: Implement Exponential Backoff**
```java
// Add to BaseApiClient.java
public <T> ApiResponse<T> executeWithRetry(RequestExecutor executor, Class<T> responseType) {
    int maxRetries = 3;
    long baseDelay = 1000; // 1 second
    
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
        try {
            return executeRequest(executor, responseType);
        } catch (Exception e) {
            if (attempt == maxRetries) {
                throw e;
            }
            
            long delay = baseDelay * (1L << (attempt - 1)); // Exponential backoff
            log.warn("Attempt {} failed, retrying in {}ms: {}", attempt, delay, e.getMessage());
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Retry interrupted", ie);
            }
        }
    }
    return null;
}
```

**Solution B: Configure Connection Pooling**
```java
// Optimize REST Assured configuration
static {
    RestAssured.config = RestAssured.config()
        .httpClient(HttpClientConfig.httpClientConfig()
            .setParam("http.connection.timeout", 10000)
            .setParam("http.socket.timeout", 30000)
            .setParam("http.conn-manager.max-total", 20)
            .setParam("http.conn-manager.max-per-route", 10));
}
```

**Solution C: Add Circuit Breaker Pattern**
```java
public class CircuitBreaker {
    private int failureCount = 0;
    private long lastFailureTime = 0;
    private final int failureThreshold = 5;
    private final long timeout = 60000; // 1 minute
    
    public boolean canExecute() {
        if (failureCount >= failureThreshold) {
            if (System.currentTimeMillis() - lastFailureTime > timeout) {
                reset();
                return true;
            }
            return false;
        }
        return true;
    }
    
    public void recordSuccess() {
        reset();
    }
    
    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
    }
    
    private void reset() {
        failureCount = 0;
        lastFailureTime = 0;
    }
}
```

---

### Issue 11: SSL/TLS Certificate Issues

**Problem:**
```bash
[ERROR] javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: 
PKIX path building failed
```

**Root Cause:** SSL certificate validation issues.

**Diagnosis:**
```bash
# Test SSL connection manually
openssl s_client -connect fakerestapi.azurewebsites.net:443

# Check certificate details
curl -vI https://fakerestapi.azurewebsites.net/api/v1/Books
```

**Solutions:**

**Solution A: For Development/Testing Only (Not for Production)**
```java
// Disable SSL verification for testing environments only
static {
    RestAssured.useRelaxedHTTPSValidation();
}
```

**Solution B: Configure Proper SSL Context**
```java
// Use proper SSL configuration
public class SSLConfig {
    public static SSLContext createAcceptAllSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new AcceptAllTrustManager()}, null);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }
    }
    
    private static class AcceptAllTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }
}
```

**Solution C: Update Certificate Store**
```bash
# Update system certificates (Ubuntu/Debian)
sudo apt-get update && sudo apt-get install ca-certificates

# macOS
brew install ca-certificates

# Update Java certificate store
keytool -import -alias fakerestapi -file certificate.crt -keystore $JAVA_HOME/lib/security/cacerts
```

---

## 🐳 Docker & Containerization Issues

### Issue 12: Docker Build Failures

**Problem:**
```bash
[ERROR] failed to solve: executor failed running [/bin/sh -c mvn clean install]: exit code: 1
```

**Root Cause:** Dependency resolution or resource issues in Docker build.

**Diagnosis:**
```bash
# Build with verbose output
docker build --progress=plain --no-cache -t bookstore-api-tests .

# Check available disk space
docker system df

# Check Docker logs
docker logs [container-id]
```

**Solutions:**

**Solution A: Optimize Dockerfile Build Context**
```dockerfile
# Create .dockerignore file
target/
.git/
*.md
.DS_Store
.idea/
*.iml
node_modules/
```

**Solution B: Fix Multi-stage Build Issues**
```dockerfile
# Ensure proper file copying between stages
FROM maven:3.9.4-eclipse-temurin-11-alpine AS build
WORKDIR /app

# Copy only necessary files first
COPY pom.xml .
COPY src ./src

# Build with offline dependencies
RUN mvn dependency:go-offline -B
RUN mvn clean compile test-compile -DskipTests=true

# Runtime stage
FROM eclipse-temurin:11-jre-alpine
# ... rest of Dockerfile
```

**Solution C: Increase Docker Resources**
```bash
# Check Docker resource limits
docker info | grep -E 'CPUs|Total Memory'

# Increase Docker memory allocation (Docker Desktop)
# Settings → Resources → Memory: 4GB+
# Settings → Resources → Disk: 20GB+
```

---

### Issue 13: Container Runtime Issues

**Problem:**
```bash
[ERROR] Container exited with non-zero status: 1
```

**Root Cause:** Runtime environment configuration or dependency issues.

**Diagnosis:**
```bash
# Run container interactively for debugging
docker run -it --entrypoint=/bin/bash bookstore-api-tests

# Check container logs
docker logs bookstore-api-tests

# Check environment variables
docker run --rm bookstore-api-tests env
```

**Solutions:**

**Solution A: Fix Environment Variables**
```bash
# Run with proper environment configuration
docker run --rm \
  -e API_BASE_URL=https://fakerestapi.azurewebsites.net \
  -e PARALLEL_THREADS=3 \
  -e TEST_SUITE=testng.xml \
  bookstore-api-tests
```

**Solution B: Debug Entrypoint Script**
```bash
# Add debugging to entrypoint.sh
#!/bin/bash
set -e
set -x  # Enable debug output

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# Add validation
if [ -z "$API_BASE_URL" ]; then
    log "ERROR: API_BASE_URL environment variable not set"
    exit 1
fi

# Test API connectivity before running tests
curl -f "$API_BASE_URL/api/v1/Books" || {
    log "ERROR: Cannot connect to API at $API_BASE_URL"
    exit 1
}
```

**Solution C: Volume Mount Issues**
```bash
# Ensure proper volume mounting
docker run --rm \
  -v $(pwd)/target/allure-results:/app/target/allure-results \
  -v $(pwd)/target/allure-report:/app/target/allure-report \
  bookstore-api-tests

# Check volume permissions
docker run --rm -v $(pwd)/target:/app/target \
  bookstore-api-tests ls -la /app/target/
```

---

## 🚀 CI/CD & Pipeline Issues

### Issue 14: GitHub Actions Build Failures

**Problem:**
```bash
Error: Process completed with exit code 1
##[error]Maven build failed
```

**Root Cause:** Environment differences, resource limitations, or configuration issues.

**Diagnosis:**
```yaml
# Add debugging to GitHub Actions workflow
- name: Debug Environment
  run: |
    echo "Java version: $(java -version)"
    echo "Maven version: $(mvn -version)"
    echo "Available memory: $(free -h)"
    echo "Disk space: $(df -h)"
```

**Solutions:**

**Solution A: Fix Maven Memory Issues in CI**
```yaml
# In .github/workflows/ci-cd-pipeline.yml
- name: Set up JDK 11
  uses: actions/setup-java@v3
  with:
    java-version: '11'
    distribution: 'temurin'

- name: Configure Maven
  run: |
    echo "MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m" >> $GITHUB_ENV

- name: Run tests with proper memory
  run: mvn clean test
  env:
    MAVEN_OPTS: -Xmx1024m -XX:MaxPermSize=256m
```

**Solution B: Handle Test Failures Gracefully**
```yaml
- name: Run Tests
  run: mvn clean test
  continue-on-error: true  # Don't fail build on test failures
  
- name: Upload Test Results
  uses: actions/upload-artifact@v3
  if: always()  # Upload even if tests failed
  with:
    name: test-results
    path: |
      target/surefire-reports/
      target/allure-results/
```

**Solution C: Optimize Build Caching**
```yaml
- name: Cache Maven dependencies
  uses: actions/cache@v3
  with:
    path: ~/.m2
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-m2-
```

---

### Issue 15: Pipeline Timeouts

**Problem:**
```bash
Error: The operation was canceled.
##[error]The job running on runner GitHub Actions XX has exceeded the maximum execution time
```

**Root Cause:** Long-running tests or infinite loops.

**Solutions:**

**Solution A: Add Timeouts to Workflow**
```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    timeout-minutes: 30  # Set job timeout
    
    steps:
    - name: Run Tests
      run: mvn clean test
      timeout-minutes: 20  # Set step timeout
```

**Solution B: Optimize Test Execution**
```yaml
- name: Run Tests in Parallel
  run: |
    mvn test -DthreadCount=4 -Dparallel=methods
    
- name: Run Only Essential Tests in PR
  if: github.event_name == 'pull_request'
  run: mvn test -Dgroups=smoke
```

**Solution C: Split Long-Running Tests**
```yaml
strategy:
  matrix:
    test-group: [smoke, regression, performance]
    
steps:
- name: Run Test Group
  run: mvn test -Dgroups=${{ matrix.test-group }}
```

---

## 📊 Reporting & Allure Issues

### Issue 16: Allure Report Generation Failures

**Problem:**
```bash
[ERROR] Failed to execute goal io.qameta.allure:allure-maven:report
```

**Root Cause:** Missing test results, configuration issues, or version conflicts.

**Diagnosis:**
```bash
# Check if allure-results exist
ls -la target/allure-results/

# Check Allure configuration
cat allure.properties

# Verify Maven plugin configuration
mvn help:describe -Dplugin=io.qameta.allure:allure-maven
```

**Solutions:**

**Solution A: Verify Test Results Exist**
```bash
# Ensure tests have run and generated results
mvn clean test  # Generate test results first
mvn allure:report  # Then generate report

# Check for test results
find target/ -name "*.json" -o -name "*.txt" | head -10
```

**Solution B: Fix Allure Configuration**
```xml
<!-- Ensure proper plugin configuration in pom.xml -->
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.12.0</version>
    <configuration>
        <reportVersion>2.24.0</reportVersion>
        <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
        <reportDirectory>${project.build.directory}/allure-report</reportDirectory>
    </configuration>
</plugin>
```

**Solution C: Manual Allure Installation**
```bash
# Install Allure CLI manually if Maven plugin fails
npm install -g allure-commandline

# Generate report manually
allure generate target/allure-results --clean -o target/allure-report
allure serve target/allure-results  # Serve locally
```

---

### Issue 17: Missing Test Steps in Allure Report

**Problem:** Allure report shows test results but missing detailed steps.

**Root Cause:** Missing `@Step` annotations or AspectJ weaving issues.

**Solutions:**

**Solution A: Add Missing Annotations**
```java
import io.qameta.allure.Step;

public class BookApiClient {
    
    @Step("Getting all books from API")
    public ApiResponse<List<Book>> getAllBooks() {
        log.info("Executing GET /api/v1/Books");
        return executeGet(config.getBooksEndpoint(), 
                (Class<List<Book>>) (Class<?>) List.class);
    }
    
    @Step("Creating new book: {book.title}")
    public ApiResponse<Book> createBook(Book book) {
        log.info("Creating book: {}", book.getTitle());
        return executePost(config.getBooksEndpoint(), book, Book.class);
    }
}
```

**Solution B: Fix AspectJ Configuration**
```xml
<!-- Ensure AspectJ weaver is properly configured -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.20/aspectjweaver-1.9.20.jar"
        </argLine>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.20</version>
        </dependency>
    </dependencies>
</plugin>
```

---

## 💾 Data & Environment Issues

### Issue 18: Test Data File Loading Failures

**Problem:**
```bash
[ERROR] java.lang.IllegalArgumentException: File not found: valid-book.json
```

**Root Cause:** Incorrect file paths or missing test data files.

**Diagnosis:**
```bash
# Check test data files exist
find src/ -name "*.json" -type f

# Check classpath resources
jar tf target/bookstore-api-automation-1.0.0.jar | grep json
```

**Solutions:**

**Solution A: Fix File Paths**
```java
// Correct resource loading
public static String readFileFromResources(String fileName) {
    try (InputStream inputStream = FileUtils.class.getClassLoader()
            .getResourceAsStream("test-data/" + fileName)) {  // Ensure correct path
        
        if (inputStream == null) {
            // Try alternative paths
            inputStream = FileUtils.class.getResourceAsStream("/test-data/" + fileName);
        }
        
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
        throw new RuntimeException("Failed to read file: " + fileName, e);
    }
}
```

**Solution B: Create Missing Test Data Files**
```bash
# Create test data directory structure
mkdir -p src/main/resources/test-data

# Create required JSON files
cat > src/main/resources/test-data/valid-book.json << 'EOF'
{
  "title": "The Great API Adventure",
  "description": "A comprehensive guide to API testing",
  "pageCount": 450,
  "excerpt": "In this book, we explore API testing...",
  "publishDate": "2024-01-15T10:30:00.000Z"
}
EOF
```

**Solution C: Add Resource Validation**
```java
@BeforeSuite
public void validateTestDataFiles() {
    String[] requiredFiles = {
        "valid-book.json",
        "invalid-book.json", 
        "valid-author.json",
        "invalid-author.json"
    };
    
    for (String fileName : requiredFiles) {
        try {
            FileUtils.readFileFromResources(fileName);
            log.info("✅ Test data file found: {}", fileName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Missing test data file: " + fileName, e);
        }
    }
}
```

---

### Issue 19: Environment Configuration Issues

**Problem:** Tests fail in different environments due to configuration mismatches.

**Root Cause:** Environment-specific configuration not properly applied.

**Diagnosis:**
```bash
# Check active Maven profile
mvn help:active-profiles

# Check effective properties
mvn help:effective-settings

# Test with specific profile
mvn test -Pstaging -X  # Debug mode
```

**Solutions:**

**Solution A: Validate Environment Configuration**
```java
@BeforeSuite
public void validateEnvironmentConfig() {
    ApiConfig config = ApiConfig.getInstance();
    
    log.info("Environment Configuration:");
    log.info("  Base URL: {}", config.getBaseUrl());
    log.info("  Timeout: {}", config.getTimeout());
    log.info("  Parallel Threads: {}", config.getParallelThreads());
    
    // Validate configuration
    if (!config.getBaseUrl().startsWith("https://")) {
        throw new RuntimeException("Invalid base URL: " + config.getBaseUrl());
    }
    
    if (config.getTimeout() <= 0) {
        throw new RuntimeException("Invalid timeout: " + config.getTimeout());
    }
}
```

**Solution B: Environment-Specific Test Selection**
```java
// Skip certain tests in specific environments
@Test
public void testCreateManyBooks() {
    String environment = System.getProperty("environment", "dev");
    if ("prod".equals(environment)) {
        throw new SkipException("Skipping bulk creation test in production");
    }
    
    // Test implementation
}
```

**Solution C: Configuration Override Mechanism**
```bash
# Override configuration at runtime
mvn test -Dapi.base.url=https://custom-api.com \
         -Dapi.timeout=60000 \
         -Dtest.parallel.threads=1
```

---

## 🚨 Emergency Procedures

### Quick Recovery Commands

**When Everything Fails:**
```bash
# Nuclear option - complete reset
rm -rf target/
rm -rf ~/.m2/repository/com/bookstore/
mvn clean install -DskipTests

# Verify basic setup
mvn compile
java -version
mvn -version
```

**Fast Test Execution for Debugging:**
```bash
# Run single test for quick feedback
mvn test -Dtest=BookApiHappyPathTests#testGetAllBooks

# Run without parallel execution
mvn test -DthreadCount=1

# Run with debug logging
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

**Quick Health Check:**
```bash
# Verify API connectivity
curl -I https://fakerestapi.azurewebsites.net/api/v1/Books

# Check framework compilation
mvn clean compile test-compile

# Run smoke tests only
mvn test -Dgroups=smoke
```

---

## 📞 Getting Additional Help

### Log Analysis

**Collect Diagnostic Information:**
```bash
# Generate comprehensive diagnostic report
./scripts/generate-diagnostics.sh > diagnostics.txt 2>&1

# Or manually collect key information
echo "=== Environment Information ===" > debug.log
java -version >> debug.log 2>&1
mvn -version >> debug.log 2>&1
echo "=== Maven Effective POM ===" >> debug.log
mvn help:effective-pom >> debug.log 2>&1
echo "=== Test Execution Output ===" >> debug.log
mvn clean test >> debug.log 2>&1
```

### Common Log Patterns

**Look for these patterns in logs:**

| Pattern | Indicates | Action |
|---------|-----------|--------|
| `Connection refused` | API unavailable | Check API status, network |
| `OutOfMemoryError` | Memory issues | Increase heap size |
| `SocketTimeoutException` | Network timeouts | Increase timeout values |
| `ClassNotFoundException` | Dependency issues | Check Maven dependencies |
| `NoSuchMethodError` | Version conflicts | Check dependency versions |
| `cannot find symbol` | Compilation issues | Check imports, dependencies |

### Escalation Contacts

**When to Escalate:**
- Framework issues affecting entire team (>2 hours unresolved)
- Infrastructure problems (API consistently unavailable)
- Critical CI/CD pipeline failures (blocking releases)
- Performance degradation >50% from baseline

**Information to Provide:**
1. **Error Description**: Exact error message and stack trace
2. **Reproduction Steps**: Minimal steps to reproduce the issue
3. **Environment Details**: OS, Java version, Maven version
4. **Recent Changes**: Any code or configuration changes
5. **Logs**: Relevant log files and diagnostic output

---

## 📚 Additional Resources

### Documentation Links
- [Framework README](README.md)
- [API Documentation](docs/API_DOCUMENTATION.md)
- [Performance Benchmarks](docs/PERFORMANCE_BENCHMARKS.md)
- [Testing Strategy](docs/TESTING_STRATEGY.md)

### External Resources
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Troubleshooting](https://maven.apache.org/guides/mini/guide-ide-eclipse.html)

### Community Support
- Stack Overflow: Tag questions with `rest-assured`, `testng`, `allure`
- GitHub Issues: Framework-specific issues and feature requests
- Maven Community: [Maven Users Mailing List](https://maven.apache.org/mailing-lists.html)

---

*This troubleshooting guide is maintained by the QA Engineering team and updated regularly based on common issues encountered by framework users. For urgent issues not covered here, please contact the development team directly.*
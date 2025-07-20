package com.bookstore.utils;

public class TestGroupConstants {

  // Test Groups
  public static final String SMOKE = "smoke";
  public static final String REGRESSION = "regression";
  public static final String INTEGRATION = "integration";
  public static final String PERFORMANCE = "performance";
  public static final String SECURITY = "security";

  // API Groups
  public static final String BOOKS = "books";
  public static final String AUTHORS = "authors";
  public static final String API = "api";

  // Test Types
  public static final String HAPPY_PATH = "happy_path";
  public static final String EDGE_CASES = "edge_cases";
  public static final String NEGATIVE = "negative";
  public static final String BOUNDARY = "boundary";

  // HTTP Methods
  public static final String GET = "get";
  public static final String POST = "post";
  public static final String PUT = "put";
  public static final String DELETE = "delete";
  public static final String PATCH = "patch";

  // Priority Levels
  public static final String CRITICAL = "critical";
  public static final String HIGH = "high";
  public static final String MEDIUM = "medium";
  public static final String LOW = "low";

  // Environment Groups
  public static final String DEV = "dev";
  public static final String STAGING = "staging";
  public static final String PROD = "prod";

  private TestGroupConstants() {
    // Utility class, prevent instantiation
  }
}

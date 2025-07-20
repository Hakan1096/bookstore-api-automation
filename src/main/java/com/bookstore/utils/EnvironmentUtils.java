package com.bookstore.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentUtils {

  public static String getEnvironmentVariable(String key, String defaultValue) {
    String value = System.getenv(key);
    if (value == null || value.trim().isEmpty()) {
      value = System.getProperty(key, defaultValue);
    }
    log.debug("Environment variable {}: {}", key, value);
    return value;
  }

  public static boolean isCI() {
    return "true".equalsIgnoreCase(getEnvironmentVariable("CI", "false")) ||
      getEnvironmentVariable("GITHUB_ACTIONS", "false").equals("true") ||
      getEnvironmentVariable("JENKINS_URL", null) != null;
  }

  public static boolean isDebugMode() {
    return "true".equalsIgnoreCase(getEnvironmentVariable("DEBUG", "false"));
  }

  public static String getCurrentEnvironment() {
    return getEnvironmentVariable("ENVIRONMENT", "dev");
  }

  public static boolean isHeadlessMode() {
    return "true".equalsIgnoreCase(getEnvironmentVariable("HEADLESS", "true"));
  }

  public static int getParallelThreads() {
    return Integer.parseInt(getEnvironmentVariable("PARALLEL_THREADS", "3"));
  }

  public static long getDefaultTimeout() {
    return Long.parseLong(getEnvironmentVariable("DEFAULT_TIMEOUT", "30000"));
  }
}
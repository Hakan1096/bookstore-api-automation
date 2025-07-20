package com.bookstore.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

@Slf4j
public class RetryUtils {

  public static <T> T retry(Callable<T> operation, int maxAttempts, long delayMs) {
    return retry(operation, maxAttempts, delayMs, result -> true);
  }

  public static <T> T retry(Callable<T> operation, int maxAttempts, long delayMs, Predicate<T> successCondition) {
    Exception lastException = null;

    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        T result = operation.call();
        if (successCondition.test(result)) {
          if (attempt > 1) {
            log.info("Operation succeeded on attempt {}/{}", attempt, maxAttempts);
          }
          return result;
        } else {
          log.warn("Operation completed but failed success condition on attempt {}/{}", attempt, maxAttempts);
        }
      } catch (Exception e) {
        lastException = e;
        log.warn("Operation failed on attempt {}/{}: {}", attempt, maxAttempts, e.getMessage());
      }

      if (attempt < maxAttempts) {
        try {
          Thread.sleep(delayMs * attempt); // Exponential backoff
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Retry interrupted", ie);
        }
      }
    }

    throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
  }

  public static void retryVoid(Runnable operation, int maxAttempts, long delayMs) {
    retry(() -> {
      operation.run();
      return true;
    }, maxAttempts, delayMs);
  }
}

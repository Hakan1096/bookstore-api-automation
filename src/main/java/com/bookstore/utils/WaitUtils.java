package com.bookstore.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Slf4j
public class WaitUtils {

  public static void waitFor(Duration timeout, Duration pollingInterval, Callable<Boolean> condition) {
    waitFor(timeout, pollingInterval, condition, "Condition not met within timeout");
  }

  public static void waitFor(Duration timeout, Duration pollingInterval, Callable<Boolean> condition, String timeoutMessage) {
    long timeoutMs = timeout.toMillis();
    long pollingMs = pollingInterval.toMillis();
    long startTime = System.currentTimeMillis();

    while (System.currentTimeMillis() - startTime < timeoutMs) {
      try {
        if (condition.call()) {
          return;
        }
      } catch (Exception e) {
        log.debug("Condition check failed: {}", e.getMessage());
      }

      try {
        Thread.sleep(pollingMs);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Wait interrupted", e);
      }
    }

    throw new RuntimeException(timeoutMessage + " (timeout: " + timeout + ")");
  }

  public static <T> T waitForValue(Duration timeout, Duration pollingInterval, Supplier<T> valueSupplier, T expectedValue) {
    waitFor(timeout, pollingInterval, () -> {
      T actualValue = valueSupplier.get();
      return expectedValue.equals(actualValue);
    }, "Expected value not found within timeout");

    return valueSupplier.get();
  }

  public static void sleep(Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Sleep interrupted", e);
    }
  }
}
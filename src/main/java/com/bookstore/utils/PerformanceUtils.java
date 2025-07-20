package com.bookstore.utils;

import io.qameta.allure.Allure;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
public class PerformanceUtils {

  @Data
  public static class PerformanceResult {
    private long totalExecutionTime;
    private long averageResponseTime;
    private long minResponseTime;
    private long maxResponseTime;
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private double successRate;
    private double throughput; // requests per second
    private List<Long> responseTimes;

    public PerformanceResult(List<Long> responseTimes, List<Boolean> results) {
      this.responseTimes = new ArrayList<>(responseTimes);
      this.totalRequests = responseTimes.size();
      this.successfulRequests = (int) results.stream().mapToInt(b -> b ? 1 : 0).sum();
      this.failedRequests = totalRequests - successfulRequests;
      this.successRate = totalRequests > 0 ? (double) successfulRequests / totalRequests * 100 : 0;

      if (!responseTimes.isEmpty()) {
        this.averageResponseTime = (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        this.minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        this.maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        this.totalExecutionTime = responseTimes.stream().mapToLong(Long::longValue).sum();
        this.throughput = totalRequests > 0 && totalExecutionTime > 0 ?
          (double) totalRequests / (totalExecutionTime / 1000.0) : 0;
      }
    }
  }

  public static PerformanceResult executeLoadTest(Callable<Boolean> operation, int numberOfRequests, int concurrency) {
    log.info("Starting load test: {} requests with {} concurrent threads", numberOfRequests, concurrency);

    ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
    List<CompletableFuture<RequestResult>> futures = new ArrayList<>();

    long startTime = System.currentTimeMillis();

    // Submit all requests
    IntStream.range(0, numberOfRequests).forEach(i -> {
      CompletableFuture<RequestResult> future = CompletableFuture.supplyAsync(() -> {
        long requestStart = System.currentTimeMillis();
        boolean success;
        try {
          success = operation.call();
        } catch (Exception e) {
          log.warn("Request {} failed: {}", i, e.getMessage());
          success = false;
        }
        long requestEnd = System.currentTimeMillis();
        return new RequestResult(requestEnd - requestStart, success);
      }, executorService);

      futures.add(future);
    });

    // Wait for all requests to complete
    List<Long> responseTimes = new ArrayList<>();
    List<Boolean> results = new ArrayList<>();

    futures.forEach(future -> {
      try {
        RequestResult result = future.get();
        responseTimes.add(result.responseTime);
        results.add(result.success);
      } catch (Exception e) {
        log.error("Error getting future result: {}", e.getMessage());
        responseTimes.add(0L);
        results.add(false);
      }
    });

    executorService.shutdown();

    long endTime = System.currentTimeMillis();
    PerformanceResult performanceResult = new PerformanceResult(responseTimes, results);
    performanceResult.setTotalExecutionTime(endTime - startTime);

    log.info("Load test completed: {} requests, {}% success rate, {} ms average response time",
      numberOfRequests, String.format("%.2f", performanceResult.getSuccessRate()),
      performanceResult.getAverageResponseTime());

    // Attach performance results to Allure report
    attachPerformanceResults(performanceResult);

    return performanceResult;
  }

  public static PerformanceResult measureResponseTime(Callable<Boolean> operation, int iterations) {
    log.info("Measuring response time over {} iterations", iterations);

    List<Long> responseTimes = new ArrayList<>();
    List<Boolean> results = new ArrayList<>();

    for (int i = 0; i < iterations; i++) {
      long startTime = System.currentTimeMillis();
      boolean success;
      try {
        success = operation.call();
      } catch (Exception e) {
        log.warn("Iteration {} failed: {}", i, e.getMessage());
        success = false;
      }
      long endTime = System.currentTimeMillis();

      responseTimes.add(endTime - startTime);
      results.add(success);
    }

    PerformanceResult result = new PerformanceResult(responseTimes, results);
    log.info("Response time measurement completed: {} ms average over {} iterations",
      result.getAverageResponseTime(), iterations);

    return result;
  }

  private static void attachPerformanceResults(PerformanceResult result) {
    StringBuilder report = new StringBuilder();
    report.append("Performance Test Results\n");
    report.append("========================\n");
    report.append(String.format("Total Requests: %d\n", result.getTotalRequests()));
    report.append(String.format("Successful Requests: %d\n", result.getSuccessfulRequests()));
    report.append(String.format("Failed Requests: %d\n", result.getFailedRequests()));
    report.append(String.format("Success Rate: %.2f%%\n", result.getSuccessRate()));
    report.append(String.format("Average Response Time: %d ms\n", result.getAverageResponseTime()));
    report.append(String.format("Min Response Time: %d ms\n", result.getMinResponseTime()));
    report.append(String.format("Max Response Time: %d ms\n", result.getMaxResponseTime()));
    report.append(String.format("Throughput: %.2f requests/second\n", result.getThroughput()));
    report.append(String.format("Total Execution Time: %d ms\n", result.getTotalExecutionTime()));

    Allure.addAttachment("Performance Test Results", "text/plain", report.toString());
  }

  @Data
  private static class RequestResult {
    private final long responseTime;
    private final boolean success;
  }
}

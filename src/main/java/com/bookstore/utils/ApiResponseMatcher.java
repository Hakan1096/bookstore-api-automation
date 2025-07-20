package com.bookstore.utils;

import com.bookstore.models.ApiResponse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ApiResponseMatcher {

  public static Matcher<ApiResponse<?>> hasStatusCode(int expectedStatusCode) {
    return new BaseMatcher<ApiResponse<?>>() {
      @Override
      public boolean matches(Object item) {
        if (!(item instanceof ApiResponse)) {
          return false;
        }
        ApiResponse<?> response = (ApiResponse<?>) item;
        return response.getStatusCode() == expectedStatusCode;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("response with status code ").appendValue(expectedStatusCode);
      }

      @Override
      public void describeMismatch(Object item, Description description) {
        if (item instanceof ApiResponse) {
          ApiResponse<?> response = (ApiResponse<?>) item;
          description.appendText("was response with status code ").appendValue(response.getStatusCode());
        } else {
          description.appendText("was not an ApiResponse");
        }
      }
    };
  }

  public static Matcher<ApiResponse<?>> isSuccess() {
    return new BaseMatcher<ApiResponse<?>>() {
      @Override
      public boolean matches(Object item) {
        if (!(item instanceof ApiResponse)) {
          return false;
        }
        ApiResponse<?> response = (ApiResponse<?>) item;
        return response.isSuccess();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("successful response");
      }

      @Override
      public void describeMismatch(Object item, Description description) {
        if (item instanceof ApiResponse) {
          ApiResponse<?> response = (ApiResponse<?>) item;
          description.appendText("was response with status ").appendValue(response.getStatusCode());
        } else {
          description.appendText("was not an ApiResponse");
        }
      }
    };
  }

  public static Matcher<ApiResponse<?>> hasResponseTimeUnder(long maxTimeMs) {
    return new BaseMatcher<ApiResponse<?>>() {
      @Override
      public boolean matches(Object item) {
        if (!(item instanceof ApiResponse)) {
          return false;
        }
        ApiResponse<?> response = (ApiResponse<?>) item;
        return response.getResponseTime() < maxTimeMs;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("response time under ").appendValue(maxTimeMs).appendText("ms");
      }

      @Override
      public void describeMismatch(Object item, Description description) {
        if (item instanceof ApiResponse) {
          ApiResponse<?> response = (ApiResponse<?>) item;
          description.appendText("was response time ").appendValue(response.getResponseTime()).appendText("ms");
        } else {
          description.appendText("was not an ApiResponse");
        }
      }
    };
  }
}

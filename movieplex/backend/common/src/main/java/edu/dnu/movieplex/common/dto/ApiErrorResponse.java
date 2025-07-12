package edu.dnu.movieplex.common.dto;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * A record representing the structure of API error responses.
 */
@Getter
public class ApiErrorResponse {
  private final int status;
  private final String message;
  private final LocalDateTime timestamp;

  /**
  * Constructs an ApiErrorResponse object with the provided status and message, initializing the
  * timestamp to the current time.
  *
  * @param status  HTTP status code associated with the exception.
  * @param message Message describing the exception.
  */
  public ApiErrorResponse(int status, String message) {
    this.status = status;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}
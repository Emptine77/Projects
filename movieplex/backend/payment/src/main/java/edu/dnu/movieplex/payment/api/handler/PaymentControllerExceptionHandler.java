package edu.dnu.movieplex.payment.api.handler;

import edu.dnu.movieplex.common.dto.ApiErrorResponse;
import edu.dnu.movieplex.common.exception.GenericException;
import edu.dnu.movieplex.payment.api.controller.PaymentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception handler for the payment module controllers.
 * Handles various exceptions and provides consistent error responses.
 */
@ControllerAdvice(basePackageClasses = PaymentController.class)
@Slf4j
public class PaymentControllerExceptionHandler {
  /**
   * Handles unexpected exceptions and returns a standardized error response.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {
    log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

    ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.");

    return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles generic exception.
   *
   * @param ex The instance of {@link GenericException} to be handled.
   * @return ResponseEntity containing the {@link ApiErrorResponse} with the HTTP status
   *     and error details.
   */
  @ExceptionHandler(GenericException.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(GenericException ex) {
    log.info(ex.getMessage());
    return new ResponseEntity<>(new ApiErrorResponse(ex.getStatus().value(), ex.getMessage()),
            ex.getStatus());
  }

  /**
   * Handles {@link MethodArgumentNotValidException} and returns a standardized error response.
   *
   * @param ex The validation exception indicating invalid method arguments.
   * @return A {@link ResponseEntity} containing the error response.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    StringBuilder sb = new StringBuilder("Validation errors:[");

    for (FieldError error : result.getFieldErrors()) {
      sb.append(error.getField())
          .append(": ")
          .append(error.getDefaultMessage())
          .append(", ");
    }
    sb.append("]");

    String message = sb.toString().replaceAll(", ]", "]");
    ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            message);
    return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
  }
}

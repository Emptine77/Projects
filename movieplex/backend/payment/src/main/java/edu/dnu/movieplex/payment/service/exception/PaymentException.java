package edu.dnu.movieplex.payment.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * An exception class representing payment-related errors.
 */
@StandardException
public class PaymentException extends GenericException {

  public PaymentException(String message) {
    super(message);
    this.status = HttpStatus.PAYMENT_REQUIRED;
  }
}

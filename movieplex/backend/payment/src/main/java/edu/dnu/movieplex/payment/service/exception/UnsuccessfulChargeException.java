package edu.dnu.movieplex.payment.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * An exception class representing errors related to unsuccessful charges.
 */
@StandardException
public class UnsuccessfulChargeException extends GenericException {
  public UnsuccessfulChargeException(String message) {
    super(message);
    this.status = HttpStatus.PAYMENT_REQUIRED;
  }
}

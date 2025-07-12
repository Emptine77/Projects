package edu.dnu.movieplex.movie.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * Exception indicating that an illegal status was encountered while processing
 * a refund code.
 */
@StandardException
public class IllegalRefundCodeStatusException extends GenericException {

  public IllegalRefundCodeStatusException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

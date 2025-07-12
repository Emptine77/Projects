package edu.dnu.movieplex.movie.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * Exception indicating that a ticket refund code could not be found.
 */
@StandardException
public class TicketRefundCodeNotFoundException extends GenericException {

  public TicketRefundCodeNotFoundException(String message) {
    super(message);
    status = HttpStatus.NOT_FOUND;
  }
}

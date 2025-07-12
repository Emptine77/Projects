package edu.dnu.movieplex.movie.persistance.model.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;


/**
 * Exception indicating an illegal or unexpected ticket status.
 */
@StandardException
public class IllegalTicketStatusException extends GenericException {

  public IllegalTicketStatusException(String message) {
    super(message);
    status = HttpStatus.BAD_REQUEST;
  }
}

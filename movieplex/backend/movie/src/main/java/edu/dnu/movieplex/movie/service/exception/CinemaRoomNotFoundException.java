package edu.dnu.movieplex.movie.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * Exception indicating that a cinema room could not be found.
 */
@StandardException
public class CinemaRoomNotFoundException extends GenericException {

  public CinemaRoomNotFoundException(String message) {
    super(message);
    status = HttpStatus.NOT_FOUND;
  }
}

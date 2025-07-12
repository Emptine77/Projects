package edu.dnu.movieplex.movie.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a movie is not found.
 */
@StandardException
public class MovieNotFoundException extends GenericException {

  public MovieNotFoundException(String message) {
    super(message);
    status = HttpStatus.NOT_FOUND;
  }
}


package edu.dnu.movieplex.user.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to logout operations. Extends GenericException.
 */
public class LogoutException extends GenericException {

    public LogoutException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }
}

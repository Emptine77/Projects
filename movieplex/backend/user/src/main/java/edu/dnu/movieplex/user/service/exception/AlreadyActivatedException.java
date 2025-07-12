package edu.dnu.movieplex.user.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import org.springframework.http.HttpStatus;

/**
 * The given exception shows that the user is activated and has no need to activate themselves
 * again. Extends GenericException.
 *
 * @author osynelnyk
 */
public class AlreadyActivatedException extends GenericException {

    public AlreadyActivatedException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }
}

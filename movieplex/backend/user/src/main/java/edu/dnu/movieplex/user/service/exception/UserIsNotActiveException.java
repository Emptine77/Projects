package edu.dnu.movieplex.user.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import org.springframework.http.HttpStatus;

/**
 * The given exception shows that the user is INACTIVE and has to verify their email address.
 * Extends GenericException.
 *
 * @author osynelnyk
 */
public class UserIsNotActiveException extends GenericException {

    public UserIsNotActiveException(String message) {
        super(message);
        status = HttpStatus.UNAUTHORIZED;
    }
}

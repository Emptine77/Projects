package edu.dnu.movieplex.user.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to Keycloak requests. Extends GenericException.
 */
public class KeycloakException extends GenericException {

    public KeycloakException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }
}

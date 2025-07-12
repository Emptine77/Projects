package edu.dnu.movieplex.user.service.exception;

import edu.dnu.movieplex.common.exception.GenericException;
import org.springframework.http.HttpStatus;

/**
 * Represents an exception specific to Feign client requests. Extends RuntimeException.
 */
public class FeignClientRequestException extends GenericException {

    public FeignClientRequestException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }
}

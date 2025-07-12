package edu.dnu.movieplex.user.api.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import edu.dnu.movieplex.common.dto.ApiErrorResponse;
import edu.dnu.movieplex.common.exception.GenericException;
import io.jsonwebtoken.JwtException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Global exception handler for handling specific exceptions and providing custom error responses.
 */
public class GlobalExceptionHandler {
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_MESSAGE =
            "The field '%s' must have a valid type of '%s'. Wrong value is '%s'";

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationFailedException(
            AuthenticationFailedException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(UNAUTHORIZED.value(), ex.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(UNAUTHORIZED.value(), ex.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(GenericException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getStatus().value(), ex.getMessage()),
                ex.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ApiErrorResponse(BAD_REQUEST.value(), ex.getMessage()), BAD_REQUEST);
    }

    /**
     * Exception handler method for handling MethodArgumentNotValidException. It processes validation
     * errors and generates a custom response entity with error details.
     *
     * @param ex The MethodArgumentNotValidException instance caught during request processing.
     * @return ResponseEntity containing AppException with details of validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleNotValidRegisterData(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder extractedMessages = new StringBuilder();

        for (String errorMessage : errors.values()) {
            String extractedMessage = errorMessage.replaceAll("\\{(.+?)=(.+?)}", "$2").trim();
            extractedMessages.append(extractedMessage).append(", ");
        }

        String finalMessage = extractedMessages.toString();
        if (finalMessage.endsWith(", ")) {
            finalMessage = finalMessage.substring(0, finalMessage.length() - 2);
        }

        return new ResponseEntity<>(new ApiErrorResponse(BAD_REQUEST.value(), finalMessage),
                BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurityException(
            SecurityException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(UNAUTHORIZED.value(), ex.getMessage()),
                UNAUTHORIZED);
    }

    /**
     * The given method intercepts ConstraintViolationException, which occurs when an argument of
     * a controller method violates restrictions, like @Min(1) and the argument was -5.
     *
     * @param ex ConstraintViolationException that is intercepted by this method.
     * @return ResponseEntity with details about ConstraintViolationException.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ApiErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> cvSet = ex.getConstraintViolations();
        List<ApiErrorResponse> errors = new ArrayList<>();
        for (var cv : cvSet) {
            String field = cv.getPropertyPath().toString()
                    .substring(cv.getPropertyPath().toString().lastIndexOf('.') + 1);
            String value = cv.getInvalidValue().toString();
            String message = String.format(cv.getMessage(), field);
            ApiErrorResponse appException = new ApiErrorResponse(BAD_REQUEST.value(),
                    "%s. Wrong value: %s".formatted(message, value));
            errors.add(appException);
        }
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    /**
     * The given method intercepts MethodArgumentTypeMismatchException, which occurs when
     * an argument of a controller method has wrong type. For example, id must have
     * a representation of Long (i.e "5"), but was inconvertible String (i.e "asdf").
     *
     * @param ex TypeMismatchException that is intercepted by this method.
     * @return ResponseEntity with details about TypeMismatchException.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String actualField = ex.getName();
        Object wrongValue = Optional.ofNullable(ex.getValue()).orElse("");
        String requiredType = "";
        Class<?> requiredTypeClass = ex.getRequiredType();
        if (requiredTypeClass != null) {
            requiredType = requiredTypeClass.getSimpleName();
        }
        String message = String.format(METHOD_ARGUMENT_TYPE_MISMATCH_MESSAGE, actualField,
                requiredType, wrongValue);
        ApiErrorResponse appException = new ApiErrorResponse(BAD_REQUEST.value(), message);
        return new ResponseEntity<>(appException, BAD_REQUEST);
    }

    /**
     * The given method intercepts DataIntegrityViolationException, which may occur when
     * json of a request lacks some necessary fields (i.e. field "price" in ProductCreateRequest),
     * so saving such entity to the database is impossible to execute.
     *
     * @param ex DataIntegrityViolationException that is intercepted by this method.
     * @return ResponseEntity with details about DataIntegrityViolationException.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        String message = Objects.requireNonNullElse(rootCause, ex).getMessage();
        return new ResponseEntity<>(new ApiErrorResponse(BAD_REQUEST.value(), message), BAD_REQUEST);
    }

    /**
     * This method is intended to handle exceptions related to the inability to
     * read the HTTP message, usually caused by issues in the request payload,
     * particularly during JSON parsing incompatible types (from 'abc' to Integer).
     *
     * @param ex HttpMessageNotReadableException that is intercepted by this method.
     * @return ResponseEntity with details about HttpMessageNotReadableException.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        String errorMessage;
        if (cause instanceof JsonMappingException jsonMappingException) {
            errorMessage = "JSON parse error: " + jsonMappingException.getOriginalMessage();

        } else {
            errorMessage = "Malformed JSON request. Check your request payload.";
        }
        return new ResponseEntity<>(new ApiErrorResponse(BAD_REQUEST.value(), errorMessage), BAD_REQUEST);
    }
}

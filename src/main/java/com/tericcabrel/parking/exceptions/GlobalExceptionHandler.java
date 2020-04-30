package com.tericcabrel.parking.exceptions;

import com.tericcabrel.parking.models.responses.ConstraintViolationResponse;
import com.tericcabrel.parking.models.responses.CustomResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.utils.Helpers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;


/**
 * This class intercepts exceptions thrown in the whole application and customize
 * either the response status code or the content to be set in the body
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @param message message to be added in the response
     *
     * @return instance of HashMap<String, Object>
     */
    private HashMap<String, Object> formatMessage(String message) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);

        return result;
    }

    /**
     * @param errors List of errors to be returned
     *
     * @return instance of InvalidDataResponse
     */
    private InvalidDataResponse createInvalidDataResponse(HashMap<String, List<String>> errors) {
        HashMap<String, HashMap<String, List<String>>> result = new HashMap<>();
        result.put("errors", errors);

        return new InvalidDataResponse(result);
    }

    /**
     * Throw when an item not found in the database
     *
     * @param ex instance of ResourceNotFoundException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Throw when the password provided by the user not match the one stored in the database
     *
     * @param ex instance of PasswordNotMatchException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<?> passwordNotMatchException(PasswordNotMatchException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when DTO validation fails on default validator (which come with javax.validation)
     *
     * Example of output to the client
     * {
     *     errors: {
     *         username: ["This field is required"],
     *         email: ["Must have at least 8 characters", "Must be a valid email address"],
     *         ....
     *     }
     * }
     *
     * @param ex instance of MethodArgumentNotValidException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 422
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        HashMap<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String field = "";

            if (objectError.getArguments() != null && objectError.getArguments().length >= 2) {
                field = objectError.getArguments()[1].toString();
            }

            if (field.length() > 0) {
                Helpers.updateErrorHashMap(errors, field, objectError.getDefaultMessage());
            }
        });

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            Helpers.updateErrorHashMap(errors, fieldError.getField(), fieldError.getDefaultMessage());
        });

        return new ResponseEntity<>(createInvalidDataResponse(errors), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Throw when DTO validation fails on custom validator (validator created by me)
     *
     *  Example of output to the client
     *  {
     *      errors: {
     *          confirmPassword: "Don't match the password",
     *          ....
     *      }
     *  }
     *
     * @param ex instance of ConstraintViolationException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 422
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HashMap<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv -> {
            String[] strings = cv.getPropertyPath().toString().split("\\.");

            errors.put(strings[strings.length - 1], cv.getMessage());
        });

        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        result.put("errors", errors);

        ConstraintViolationResponse response = new ConstraintViolationResponse(result);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Throw when authenticated user try to access to a resource in which he didn't have required role
     *
     * @param ex instance of AccessDeniedException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Throw when authentication failed due to bad credentials
     *
     * @param ex instance of BadCredentialsException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 401 instead 500
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Throw when authenticated user is not enabled
     *
     * @param ex instance of UserNotActiveException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<CustomResponse> userNotActiveException(UserNotActiveException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when token validation fails
     *
     * @param ex instance of TokenErrorException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(TokenErrorException.class)
    public ResponseEntity<CustomResponse> tokenErrorException(TokenErrorException ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when unhandled exception is raised
     *
     * @param ex instance of Exception
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        CustomResponse response = new CustomResponse(formatMessage(ex.getMessage()));

        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.tericcabrel.parking.exceptions;

import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.utils.Helpers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

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
     * Throw when an item already exists in the database
     *
     * @param ex instance of ResourceAlreadyExistsException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> resourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when an item not found in the database
     *
     * @param ex instance of ResourceNotFoundException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 404
     */
    @ExceptionHandler({ ResourceNotFoundException.class, UsernameNotFoundException.class })
    public ResponseEntity<?> resourceNotFoundException(RuntimeException ex, WebRequest request) {
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

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
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

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

        /*ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String field = "";

            if (objectError.getArguments() != null && objectError.getArguments().length >= 2) {
                field = objectError.getArguments()[1].toString();
            }

            if (field.length() > 0) {
                Helpers.updateErrorHashMap(errors, field, objectError.getDefaultMessage());
            }
        });*/

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            Helpers.updateErrorHashMap(errors, fieldError.getField(), fieldError.getDefaultMessage());
        });

        return new ResponseEntity<>(createInvalidDataResponse(errors), HttpStatus.UNPROCESSABLE_ENTITY);
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
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

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
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));
        System.out.println("Raaa");
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
    public ResponseEntity<GenericResponse> userNotActiveException(UserNotActiveException ex, WebRequest request) {
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when pricing policy validation fails
     *
     * @param ex instance of PricingPolicyValidationErrorException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(PricingPolicyValidationErrorException.class)
    public ResponseEntity<GenericResponse> pricingPolicyValidationErrorException(
        PricingPolicyValidationErrorException ex, WebRequest request
    ) {
        HashMap<String, Object> content = formatMessage(ex.getMessage());

        content.put("validationType", ex.getValidationType());

        GenericResponse response = new GenericResponse(content);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Throw when there is no parking's lot to assign to the car of customer
     *
     * @param ex instance of NoParkingSlotAvailableException
     * @param request instance of WebRequest
     *
     * @return ResponseEntity with status code 400
     */
    @ExceptionHandler(NoParkingSlotAvailableException.class)
    public ResponseEntity<GenericResponse> noParkingSlotAvailableException(
        NoParkingSlotAvailableException ex, WebRequest request
    ) {
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

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
        GenericResponse response = new GenericResponse(formatMessage(ex.getMessage()));

        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

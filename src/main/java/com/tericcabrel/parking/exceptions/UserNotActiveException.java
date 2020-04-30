package com.tericcabrel.parking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserNotActiveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotActiveException(String message){
        super(message);
    }
}
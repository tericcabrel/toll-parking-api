package com.tericcabrel.parking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TokenErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenErrorException(String message){
        super(message);
    }
}
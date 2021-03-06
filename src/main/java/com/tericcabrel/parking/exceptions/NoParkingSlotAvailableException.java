package com.tericcabrel.parking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoParkingSlotAvailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoParkingSlotAvailableException(String message){
        super(message);
    }
}
package com.tericcabrel.parking.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PricingPolicyValidationErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String validationType;

    public PricingPolicyValidationErrorException(String message, String validationType){
        super(message);

        this.validationType = validationType;
    }
}
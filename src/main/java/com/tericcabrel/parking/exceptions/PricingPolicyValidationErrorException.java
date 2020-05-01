package com.tericcabrel.parking.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Setter
@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PricingPolicyValidationErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String validationType;

    public PricingPolicyValidationErrorException(String message, String validationType){
        super(message);

        this.validationType = validationType;
    }
}
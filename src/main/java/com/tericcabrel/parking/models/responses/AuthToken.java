package com.tericcabrel.parking.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public class AuthToken {
    private String accessToken;
    private long expiresIn;
}



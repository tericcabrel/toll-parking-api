package com.tericcabrel.parking.configs;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

import static com.tericcabrel.parking.utils.Constants.UNAUTHORIZED_MESSAGE;

/**
 * Used to customize the behavior when an error occurs in CustomAuthenticationFilter
 */
@Component
public class UnauthorizedRequestHandler implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED_MESSAGE);
    }
}

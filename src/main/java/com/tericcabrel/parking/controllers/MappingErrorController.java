package com.tericcabrel.parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Override the default route "/error" to provide more useful information about the error
 * instead of a White Label page
 */
@ApiIgnore // We don't want swagger to index this controller
@RestController
public class MappingErrorController implements ErrorController {
    private static final String TRACE_KEY = "trace";

    private final ErrorAttributes errorAttributes;

    @Autowired
    public MappingErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error")
    public Map<String, Object> error(WebRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));

        body.computeIfPresent(TRACE_KEY, (key, val) -> val.toString().split("\n\t"));

        return body;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private boolean getTraceParameter(WebRequest request) {
        String parameter = request.getParameter(TRACE_KEY);
        if (parameter == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(parameter);
    }

    private Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
        return errorAttributes.getErrorAttributes(request, includeStackTrace);
    }
}

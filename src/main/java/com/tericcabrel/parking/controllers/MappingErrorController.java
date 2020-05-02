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
    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes;

    @Autowired
    public MappingErrorController(ErrorAttributes errorAttributes) {
        // Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = PATH)
    public Map<String, Object> error(WebRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));

        String trace = (String) body.get("trace");

        if(trace != null) {
            String[] lines = trace.split("\n\t");
            body.put("trace", lines);
        }

        return body;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private boolean getTraceParameter(WebRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
        return errorAttributes.getErrorAttributes(request, includeStackTrace);
    }
}

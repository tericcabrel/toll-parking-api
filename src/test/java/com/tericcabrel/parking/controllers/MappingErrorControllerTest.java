package com.tericcabrel.parking.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MappingErrorControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ErrorAttributes errorAttributes;

    @Autowired
    private MappingErrorController mappingErrorController;

    @Test
    void template() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("trace", "Capacho");

        when(errorAttributes.getErrorAttributes(any(WebRequest.class), anyBoolean())).thenReturn(attributes);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("trace", "macha");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Object> result = restTemplate.postForEntity("/error", request, Object.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        verify(errorAttributes).getErrorAttributes(any(WebRequest.class), anyBoolean());
    }

    @Test
    void testErrorPath() {
        String path = mappingErrorController.getErrorPath();

        assertThat(path).isEqualTo("/error");
    }
}
package com.tericcabrel.parking.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void template() {
        ResponseEntity<String> response = restTemplate.getForEntity("/template", String.class);

        String content = response.getBody();

        assertThat(content).contains("+33693642889"); // Customer phone
        assertThat(content).contains("Sansa Stark"); // Customer name
        assertThat(content).contains("Parking Slot C"); // Parking slot name
        assertThat(content).contains("Gasoline"); // Car's type name
    }
}
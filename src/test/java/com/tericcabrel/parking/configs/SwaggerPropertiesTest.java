package com.tericcabrel.parking.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SwaggerPropertiesTest {
    @Autowired
    private SwaggerProperties swaggerProperties;

    @Test
    void testProperties() {
        assertThat(swaggerProperties.getTitle()).isNotNull();
    }
}
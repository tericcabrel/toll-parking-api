package com.tericcabrel.parking.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class SwaggerPropertiesTest {
    @Autowired
    private SwaggerProperties swaggerProperties;

    @Test
    void testProperties() {
        assertThat(swaggerProperties.getApiVersion()).isNotNull().isEqualTo("1.0");
        assertThat(swaggerProperties.getEnabled()).isNotNull().isEqualTo("true");
        assertThat(swaggerProperties.getTitle()).isNotNull().isEqualTo("Toll Parking Library");

        assertThat(swaggerProperties.getDescription()).isNotNull().isEqualTo("REST API to manage parking slot");
        assertThat(swaggerProperties.getUseDefaultResponseMessages()).isNotNull().isEqualTo("false");
        assertThat(swaggerProperties.getEnableUrlTemplating()).isNotNull().isEqualTo("false");
        assertThat(swaggerProperties.getDeepLinking()).isNotNull().isEqualTo("true");
        assertThat(swaggerProperties.getDefaultModelExpandDepth()).isNotNull().isEqualTo("1");
        assertThat(swaggerProperties.getDefaultModelsExpandDepth()).isNotNull().isEqualTo("1");
        assertThat(swaggerProperties.getDisplayOperationId()).isNotNull().isEqualTo("false");
        assertThat(swaggerProperties.getDisplayRequestDuration()).isNotNull().isEqualTo("false");
        assertThat(swaggerProperties.getFilter()).isNotNull().isEqualTo("false");
        assertThat(swaggerProperties.getMaxDisplayedTags()).isNotNull().isEqualTo("0");
        assertThat(swaggerProperties.getShowExtensions()).isNotNull().isEqualTo("false");
    }
}
package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.PricingPolicy;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PricingPolicyServiceImplTest {
    private PricingPolicyServiceImpl pricingPolicyService;

    private PricingPolicy pricingPolicy;

    @BeforeAll
    void beforeAll() {
        pricingPolicyService = new PricingPolicyServiceImpl();

        HashMap<String, Float>  badParameters = new HashMap<>();
        badParameters.put("pricePerHour", 100f);
        badParameters.put("nbOfHour", 3f);
        badParameters.put("tax", 200f);

        pricingPolicy = PricingPolicy.builder()
                                .parameters(badParameters)
                                .evaluation("(pricePerHour * numberOfHour) + tax")
                                .build();
    }

    @Test
    @Order(1)
    void failToValidateFormat() {
        boolean isValid = pricingPolicyService.validateFormat(pricingPolicy);

        assertThat(isValid).isFalse();
    }

    @Test
    @Order(2)
    void validateFormatSuccess() {
        HashMap<String, Float>  parameters = new HashMap<>();
        parameters.put("pricePerHour", 100f);
        parameters.put("numberOfHour", 3f);
        parameters.put("tax", 200f);

        pricingPolicy.setParameters(parameters);

        boolean isValid = pricingPolicyService.validateFormat(pricingPolicy);

        assertThat(isValid).isTrue();
    }


}
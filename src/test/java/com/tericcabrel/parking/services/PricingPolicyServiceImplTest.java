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

        pricingPolicy = PricingPolicy.builder()
                                .evaluation("(pricePerHour * numberOfHour) + tax")
                                .build();
    }

    @AfterEach
    void afterEach() {
        HashMap<String, Double> goodParameters = new HashMap<>();
        goodParameters.put("pricePerHour", 100d);
        goodParameters.put("numberOfHour", -1d);
        goodParameters.put("tax", 200d);

        pricingPolicy.setParameters(goodParameters);
    }

    @Test
    @Order(1)
    void failToValidateFormat() {
        HashMap<String, Double> badParameters = new HashMap<>();
        badParameters.put("pricePerHour", 100d);
        badParameters.put("nbOfHour", -1d);
        badParameters.put("tax", 200d);

        pricingPolicy.setParameters(badParameters);

        boolean isValid = pricingPolicyService.validateFormat(pricingPolicy);

        assertThat(isValid).isFalse();
    }

    @Test
    @Order(2)
    void validateFormatSuccess() {
        boolean isValid = pricingPolicyService.validateFormat(pricingPolicy);

        assertThat(isValid).isTrue();
    }

    @Test
    @Order(3)
    void getArithmeticalExpressionSuccess() {
        HashMap<String, Double> userParameters = new HashMap<>();
        userParameters.put("pricePerHour", 100d);
        userParameters.put("numberOfHour", 3d);
        userParameters.put("tax", 200d);

        String result = pricingPolicyService.getArithmeticalExpression(pricingPolicy, userParameters);

        assertThat(result).isNotNull();
        assertThat(result.replaceAll(" ", "")).hasSize(17);
    }


}
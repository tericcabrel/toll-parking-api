package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.PricingPolicy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
                                .evaluation("pricePerHour * numberOfHour + tax")
                                .build();
    }

    @BeforeEach
    void beforeEach() {
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
    void getArithmeticExpressionSuccess() {
        HashMap<String, Double> userParameters = new HashMap<>();
        userParameters.put("pricePerHour", 100d);
        userParameters.put("numberOfHour", 3d);
        userParameters.put("tax", 200d);

        String result = pricingPolicyService.getArithmeticExpression(pricingPolicy, userParameters);

        assertThat(result).isNotNull();
        assertThat(result.replaceAll(" ", "")).hasSize(15);
    }


    @Order(4)
    @ParameterizedTest
    @ValueSource(strings = { "100*2+6", "100*2*12", "100*(2+12)+(3-2)", "100*(2+12)/14", "3.78+4.78*(1.55)", "15-23*46" })
    void validateArithmeticExpressionSuccess(String expression) {
        boolean result = pricingPolicyService.validateArithmeticExpression(expression);

        assertThat(result).isTrue();
    }


    @Order(5)
    @ParameterizedTest
    @ValueSource(strings = { "(2+)", "10+2*+6", "(4+5", "6+5-5)", "+4-5", "33*45/", "*6-", "+*9", "4.75)+5.25-*55" })
    void failToValidateArithmeticExpression(String expression) {
        boolean result = pricingPolicyService.validateArithmeticExpression(expression);

        assertThat(result).isFalse();
    }

    @Test
    @Order(6)
    void calculatePricingPolicySuccess() {
        HashMap<String, Double> userParameters = new HashMap<>();
        userParameters.put("pricePerHour", 100d);
        userParameters.put("numberOfHour", 3d);
        userParameters.put("tax", 200d);

        Double result = pricingPolicyService.calculate(pricingPolicy, userParameters);

        assertThat(result).isEqualTo(500);
    }

}
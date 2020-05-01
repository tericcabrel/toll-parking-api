package com.tericcabrel.parking.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ArithmeticExpressionEvaluationTest {

    @ParameterizedTest
    @ValueSource(strings = { "100*2+6", "100*2*12", "100*(2+12)+(3-2)", "100*(2+12)/14", "3.78+4.78*(1.55)", "15-23*46" })
    void evaluate(String expression) {
        double value = ArithmeticExpressionEvaluation.evaluate(expression);

        assertThat(value).isIn(206d, 2400d, 1401d, 100d, 11.189d, -1043d);
    }
}
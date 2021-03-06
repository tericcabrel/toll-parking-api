package com.tericcabrel.parking.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArithmeticExpressionEvaluationTest {

    @ParameterizedTest
    @ValueSource(strings = { "100*2+6", "100*2*12", "100*(2+12)+(3-2)", "100*(2+12)/14", "3.78+4.78*(1.55)", "15-23*46" })
    void evaluate(String expression) {
        double value = ArithmeticExpressionEvaluation.evaluate(expression);

        assertThat(value).isIn(206d, 2400d, 1401d, 100d, 11.189d, -1043d);
    }

    @Nested
    class TestApplyOp {
        @Test
        void testAdditionOperator() {
            double result = ArithmeticExpressionEvaluation.applyOp('+', 14d, 25d);

            assertThat(result).isEqualTo(39d);
        }

        @Test
        void testSubtractionOperator() {
            double result = ArithmeticExpressionEvaluation.applyOp('-', 25d, 74d);

            assertThat(result).isEqualTo(49d);
        }

        @Test
        void testMultiplicationOperator() {
            double result = ArithmeticExpressionEvaluation.applyOp('*', 14d, 25d);

            assertThat(result).isEqualTo(350d);
        }

        @Test
        void testDivisionOperator() {
            double result = ArithmeticExpressionEvaluation.applyOp('/', 25d, 500d);

            assertThat(result).isEqualTo(20d);
        }

        @Test
        void testDivisionOperatorByZero() {
            assertThrows(UnsupportedOperationException.class, () -> {
                double result = ArithmeticExpressionEvaluation.applyOp('/', 0d, 25d);
            });
        }

        @Test
        void testUnknownOperator() {
            double result = ArithmeticExpressionEvaluation.applyOp('#', 14d, 25d);

            assertThat(result).isEqualTo(0d);
        }
    }
}
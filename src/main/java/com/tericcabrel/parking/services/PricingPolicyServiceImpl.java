package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.PricingPolicy;
import com.tericcabrel.parking.services.interfaces.PricingPolicyService;
import com.tericcabrel.parking.utils.ArithmeticExpressionEvaluation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

@Service("pricingPolicyService")
public class PricingPolicyServiceImpl implements PricingPolicyService {

    /**
     * @return evaluation property with the parameters replaced by numeric value
     */
    @Override
    public String getArithmeticExpression(PricingPolicy pricingPolicy, HashMap<String, Double> parameters) {
        HashMap<String, Double> defaultParameters = pricingPolicy.getParameters();
        String evaluation = pricingPolicy.getEvaluation();

        Set<String> keys = pricingPolicy.getParameters().keySet();

        // Remove all the strings in the evaluation
        for (String key: keys) {
            double value = defaultParameters.get(key);

            if (parameters.containsKey(key)) {
                value = parameters.get(key);
            }

            if (value < 0) {
                // TODO Throw an exception
            }

            evaluation = evaluation.replaceAll(key, String.valueOf(value));
        }

        return evaluation;
    }

    /**
     * @return <code>true</code> if the parameters and evaluation are valid otherwise <code>false</code>
     */
    @Override
    public boolean validateFormat(PricingPolicy pricingPolicy) {
        String pattern = "[\\(\\)\\/\\+\\*\\s-]+";

        String evaluation = pricingPolicy.getEvaluation();

        Set<String> keys = pricingPolicy.getParameters().keySet();

        // Remove all the strings in the evaluation
        for (String key: keys) {
            evaluation = evaluation.replaceAll(key, "");
        }

        // After the strings removed it will remain only +, -, *, ,/, (, ). if not, it's means the format is not valid
        return Pattern.matches(pattern, evaluation);
    }

    /**
     * @return <code>true</code> if evaluation replaced by numeric value is a valid arithmetical expression
     */
    @Override
    public boolean validateArithmeticExpression(String expression) {
        String pattern = "(([\\(])?(\\d+|\\d+\\.\\d+)([\\+\\*\\-\\/])?(\\d+|\\d+\\.\\d+)?([\\)])?([\\*\\+\\-\\/])?)+";
        String expressionWithNoSpace = expression.replaceAll(" ", "");

        boolean result = Pattern.matches(pattern, expressionWithNoSpace);

        if (!result) return result;

        result = !hasSuccessiveOperator(expressionWithNoSpace);

        if (!result) return result;

        result = !endWithOperator(expressionWithNoSpace);

        if (!result) return result;

        result = isParenthesisMatch(expressionWithNoSpace);

        if (!result) return result;

        result = isCountParenthesisMatch(expressionWithNoSpace);

        if (!result) return result;

        return result;
    }

    /**
     * @param parameters parameters containing the values to be used for calculation
     *
     * @return The price
     */
    @Override
    public double calculate(PricingPolicy pricingPolicy, HashMap<String, Double> parameters) {
        // Validate format
        boolean isValidFormat = validateFormat(pricingPolicy);

        if (!isValidFormat) {
            // TODO throw exception
        }

        // get arithmetical expression
        String expression = getArithmeticExpression(pricingPolicy, parameters);

        // Validate expression
        boolean isValidExpression = validateArithmeticExpression(expression);

        if (!isValidExpression) {
            // TODO throw exception
        }

        // Evaluate arithmetical expression
        return ArithmeticExpressionEvaluation.evaluate(expression);
    }

    /**
     * @param expression Expression to evaluate
     *
     * @return true if has successive operator otherwise false
     */
    private boolean hasSuccessiveOperator(String expression) {
        int expressionLength = expression.length();
        int charCount = 0;
        String operators = "+-*/()";
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < expressionLength; i++) {
            if (operators.indexOf(expression.charAt(i)) >= 0) {
                charCount += 1;

                builder.append(expression.charAt(i));
            } else {
                charCount = 0;
                builder.delete(0, builder.length());
            }

            if (charCount == 2) {
                String validPattern = "^[\\+\\/\\*-]\\(|\\)[\\+\\/\\*-]$"; // +( or )*

                if (Pattern.matches(validPattern, builder.toString())) {
                    charCount = 0;

                    builder.delete(0, builder.length() - 1);
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param expression Expression to evaluate
     *
     * @return true if end with any operators otherwise false
     */
    private boolean endWithOperator(String expression) {
        String operators = "+-*/(";

        return operators.indexOf(expression.charAt(expression.length() - 1)) >= 0;
    }

    /**
     * @param expression Expression to evaluate
     *
     * @return true parenthesis match in the expression
     */
    public static boolean isParenthesisMatch(String expression) {
        Map<Character, Character> matches = new HashMap<>();
        matches.put('(', ')');

        Stack<Character> stack = new Stack<>();

        for (char c: expression.toCharArray()) {
            // Check if it's a left character add in the stack
            if (matches.containsKey(c)) {
                stack.push(c);
            } else {
                // If the stack is not empty, it's invalid
                if (!stack.empty()) {
                    // If the last inserted in the stack is the key in th map which the value is equal to
                    // the current character it's means it matches so we remove it
                    if (matches.get(stack.peek()) == c) {
                        stack.pop();
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return stack.size() == 0;
    }

    /**
     * @param expression Expression to evaluate
     *
     * @return true parenthesis match in the expression
     */
    public static boolean isCountParenthesisMatch(String expression) {
        Map<Character, Integer> parenthesisCounter = new HashMap<>();
        parenthesisCounter.put('(', 0);
        parenthesisCounter.put(')', 0);

        for (char c : expression.toCharArray()) {
            if(c == '(' || c == ')') {
                parenthesisCounter.put(c, parenthesisCounter.getOrDefault(c, 0) + 1);
            }
        }

        return parenthesisCounter.get('(').equals(parenthesisCounter.get(')'));
    }
}

package com.tericcabrel.parking.utils;

import java.util.Stack;

/**
 * A Java program to evaluate a given expression where tokens are separated by space.
 *
 * Test Cases:
 * "10+2*6"        ---> 22
 * "100*2+12"      ---> 212
 * "100*(2+12)"    ---> 1400
 * "100*(2+12)/14" ---> 100
 *
 * @link https://www.geeksforgeeks.org/expression-evaluation/
 *
 * The version in the link above handle only integer and has whitespace between each number or operator
 * I updated it to handle double and remove whitespaces
 */
public class ArithmeticExpressionEvaluation
{
    // Hide the implicit public constructor
    private ArithmeticExpressionEvaluation() {
        throw new IllegalStateException("ArithmeticExpressionEvaluation class");
    }

    public static double evaluate(String expression)
    {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++)
        {
            // Current token is a number, push it to stack for numbers
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')
            {
                StringBuilder sbuf = new StringBuilder();
                // There may be more than one digits in number
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }

                values.push(Double.parseDouble(sbuf.toString()));

                // Just remove this if you want to handle whitespace between numbers and operators
                if (i < tokens.length) {
                    i--;
                }
            }
            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);

            }
            // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/')
            {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false. ( *
    public static boolean hasPrecedence(char op1, char op2)
    {
        if (op2 == '(' || op2 == ')')
            return false;
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    public static double applyOp(char op, double b, double a)
    {
        switch (op)
        {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            default:
                return 0;
        }
    }
}


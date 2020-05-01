package com.tericcabrel.parking.utils;

import java.util.Stack;

/**
 * A Java program to evaluate a given expression where tokens are separated by space.
 *
 * Test Cases:
 * "10+2*6"            ---> 22
 * "100*2+12"          ---> 212
 * "100*(2+12)"      ---> 1400
 * "100*(2+12)/14" ---> 100
 *
 * @link https://www.geeksforgeeks.org/expression-evaluation/
 *
 * The version in the link above handle only integer and has whitespace between each number or operator
 * I updated it to handle double and remove whitespaces
 */
public class ArithmeticExpressionEvaluation
{
    public static double evaluate(String expression)
    {
        System.out.println(expression);
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<Double>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++)
        {
            //System.out.print(tokens[i]);
            //System.out.println();

            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            System.out.println("v => " + tokens[i]);

            // Current token is a number, push it to stack for numbers
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')
            {
                StringBuffer sbuf = new StringBuffer();
                // There may be more than one digits in number
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }

                // System.out.println(sbuf.toString()+ " -- " + sbuf.toString().length());
                values.push(Double.parseDouble(sbuf.toString()));
                if (i < tokens.length) {
                    // System.out.println("last = " + i + " => " + tokens[i]);
                    // if (tokens[i] == ')') {
                        i--;
                    // }
                }
            }
            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);

            }
            // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                System.out.println("Mince");
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();

                System.out.println("p -> "+ values.size());
                values.stream().forEach(v -> System.out.print(v + " "));

                System.out.println("p -> " + ops.size());
                ops.stream().forEach(o -> System.out.print(o + " "));
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/')
            {
                System.out.println("Token => " + tokens[i]);
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                    System.out.print("- ");
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                System.out.println("\n");
                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        values.stream().forEach(v -> System.out.print(v + " "));
        System.out.println();

        ops.stream().forEach(o -> System.out.print(o + " "));
        System.out.println();
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
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
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
                    throw new
                        UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}


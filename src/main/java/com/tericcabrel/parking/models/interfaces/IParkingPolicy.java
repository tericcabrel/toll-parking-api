package com.tericcabrel.parking.models.interfaces;

import java.util.HashMap;

public interface IParkingPolicy {
    String  getArithmeticalExpresion();

    boolean validateFormat();

    boolean validateExpression();

    float calculate(HashMap<String, Float> parameters);
}

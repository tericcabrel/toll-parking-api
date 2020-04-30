package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.PricingPolicy;

import java.util.HashMap;

public interface PricingPolicyService {
    String  getArithmeticalExpression(PricingPolicy pricingPolicy, HashMap<String, Double> parameters);

    boolean validateFormat(PricingPolicy pricingPolicy);

    boolean validateExpression(String expression);

    double calculate(PricingPolicy pricingPolicy, HashMap<String, Double> parameters);
}

package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.PricingPolicy;

import java.util.HashMap;

public interface PricingPolicyService {
    String  getArithmeticalExpression(PricingPolicy pricingPolicy);

    boolean validateFormat(PricingPolicy pricingPolicy);

    boolean validateExpression(String expression);

    float calculate(PricingPolicy pricingPolicy, HashMap<String, Float> parameters);
}

package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.PricingPolicy;

import java.util.Map;

public interface PricingPolicyService {
    String  getArithmeticExpression(PricingPolicy pricingPolicy, Map<String, Double> parameters);

    boolean validateFormat(PricingPolicy pricingPolicy);

    boolean validateArithmeticExpression(String expression);

    double calculate(PricingPolicy pricingPolicy, Map<String, Double> parameters);
}

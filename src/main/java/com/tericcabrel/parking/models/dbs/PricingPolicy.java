package com.tericcabrel.parking.models.dbs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class PricingPolicy {
    /**
     * Contains the parameters to build pricing evaluation. here is and exeample
     * {
     *     "nbHour": -1,
     *     "pricePerHour": 30.25,
     *     "tax": 42
     * }
     * Keys with value -1 indicates the value will be provided at moment of calculation
     */
    private Map<String, Double> parameters;

    /**
     * Describe formula to calculate the price. It's an arithmetical expression who will be parsed and evaluated
     * to get the price according to the parameters provided
     *
     * Example of the value based on parameters defined as example in parameters property:
     * (nbHour * pricePerHour) + tax   Will be evaluated to : (<nbHour_provided_at_calculation> * 30.25) + 42
     */
    private String evaluation;

    public PricingPolicy() {
        this.parameters = new HashMap<>();
    }

    @Builder
    public PricingPolicy(Map<String, Double> parameters, String evaluation) {
        this.parameters = parameters;
        this.evaluation = evaluation;
    }
}

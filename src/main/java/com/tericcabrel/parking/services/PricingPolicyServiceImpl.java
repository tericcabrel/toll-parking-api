package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.PricingPolicy;
import com.tericcabrel.parking.services.interfaces.PricingPolicyService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

@Service("pricingPolicyService")
public class PricingPolicyServiceImpl implements PricingPolicyService {

    /**
     * @return evaluation property with the parameters replaced by numeric value
     */
    @Override
    public String getArithmeticalExpression(PricingPolicy pricingPolicy) {
        return null;
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
    public boolean validateExpression(String expression) {
        return false;
    }

    /**
     * @param parameters parameters containing the values to be used for calculation
     *
     * @return The price
     */
    @Override
    public float calculate(PricingPolicy pricingPolicy, HashMap<String, Float> parameters) {
        // TODO validate format

        // TODO get arithmetical expression

        // TODO validate expression

        // TODO Evaluate arithmetical expression

        return 0;
    }
}

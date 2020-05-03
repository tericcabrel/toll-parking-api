package com.tericcabrel.parking.constraints.validators;

import com.tericcabrel.parking.constraints.FieldMatch;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validate that two fields has the same value
 * Eg: password and confirmPassword
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @SneakyThrows
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
        final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

        return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
    }
}

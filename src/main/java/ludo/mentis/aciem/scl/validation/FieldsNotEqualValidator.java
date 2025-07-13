package ludo.mentis.aciem.scl.validation;

import java.util.Objects;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldsNotEqualValidator implements ConstraintValidator<FieldsNotEqual, Object> {

    private String field;
    private String secondField;

    @Override
    public void initialize(FieldsNotEqual constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.secondField = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // Use Spring's BeanWrapper to safely access properties
        var beanWrapper = new BeanWrapperImpl(value);
        Object firstFieldValue = beanWrapper.getPropertyValue(field);
        Object secondFieldValue = beanWrapper.getPropertyValue(secondField);
        
        // The validation passes if the fields are not equal.
        // Objects.equals is null-safe.
        return !Objects.equals(firstFieldValue, secondFieldValue);
    }
}
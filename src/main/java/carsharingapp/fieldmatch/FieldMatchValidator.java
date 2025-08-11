package carsharingapp.fieldmatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.apache.commons.beanutils.BeanUtils;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            String first = BeanUtils.getProperty(value, firstFieldName);
            String second = BeanUtils.getProperty(value, secondFieldName);
            return first != null && Objects.equals(first, second);
        } catch (Exception e) {
            return false;
        }
    }
}

package mate.academy.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatcher implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.fields()[0];
        this.fieldMatch = constraintAnnotation.fields()[1];
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        Object field = new BeanWrapperImpl(value).getPropertyValue(this.field);
        Object fieldMatch = new BeanWrapperImpl(value).getPropertyValue(this.fieldMatch);
        return Objects.equals(field, fieldMatch);
    }
}

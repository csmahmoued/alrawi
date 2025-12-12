package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PassportValidator implements ConstraintValidator<Passport, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return value.matches("^[A-Z][0-9]{8}$");
    }
}

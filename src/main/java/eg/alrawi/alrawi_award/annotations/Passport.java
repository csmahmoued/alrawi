package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PassportValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Passport {

    String message() default "{invalid.passport.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NationalIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NationalId {

    String message() default "Invalid National ID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

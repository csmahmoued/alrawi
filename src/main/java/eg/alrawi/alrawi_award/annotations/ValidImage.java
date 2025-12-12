package eg.alrawi.alrawi_award.annotations;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default "Invalid image file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    long maxSize() default 5 * 1024 * 1024;


    String[] allowedTypes() default { "image/jpeg", "image/png", "image/jpg" };
}

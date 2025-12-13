package eg.alrawi.alrawi_award.annotations;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default "File must be a valid image (JPEG/PNG) under 5 MB";

    long maxSize() default 5 * 1024 * 1024; // default 5 MB

    String[] allowedTypes() default { "image/jpeg", "image/png", "image/jpg" };

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

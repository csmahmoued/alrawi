package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PdfFileValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPdf {

    String message() default "File must be a PDF and ≤ 15MB";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

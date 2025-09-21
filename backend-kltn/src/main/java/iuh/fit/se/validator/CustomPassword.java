package iuh.fit.se.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPassword {
    String message() default "Ngày sinh phải trước ngày hiện tại";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
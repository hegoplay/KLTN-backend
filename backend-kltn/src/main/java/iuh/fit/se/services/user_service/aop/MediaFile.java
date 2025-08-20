package iuh.fit.se.services.user_service.aop;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MediaFileValidator.class)
@Documented
public @interface MediaFile {
    String message() default "Chỉ chấp nhận file media (hình ảnh, video, audio)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Có thể thêm các tham số tùy chọn như max size, v.v.
    long maxSize() default 10 * 1024 * 1024; // Default 10MB
}
package iuh.fit.se.services.user_service.aop;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class MediaFileValidator implements ConstraintValidator<MediaFile, MultipartFile> {

    private long maxSize;
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        // Images
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
        // Videos
        "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo",
        // Audio
        "audio/mpeg", "audio/wav", "audio/ogg", "audio/webm"
    );

    @Override
    public void initialize(MediaFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Để @NotNull hoặc @NotEmpty xử lý trường hợp này
        }

        // Kiểm tra kích thước file
        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Kích thước file không được vượt quá " + maxSize + " bytes")
                   .addConstraintViolation();
            return false;
        }

        // Kiểm tra content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Chỉ chấp nhận file media. Định dạng " + contentType + " không được hỗ trợ")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
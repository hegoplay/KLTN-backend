package iuh.fit.se.services.post_service.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO dùng để cập nhật bình luận")
public record CommentUpdateRequestDto(
    @Length(min = 1, max = 2000, message = "content phải có độ dài từ 1 đến 2000 ký tự")
    @Schema(
        description = "Nội dung của bình luận",
        example = "Bài viết này rất hay và hữu ích!",
        minLength = 1,
        maxLength = 2000
    )
    @NotBlank(message = "content không được phép trống")
	String content
	) {

}

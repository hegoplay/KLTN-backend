package iuh.fit.se.services.post_service.dto;

import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO dùng để tạo mới bình luận cho bài viết")
public record CommentCreateRequestDto(
    @NotBlank(message = "content không được phép trống")
    @Length(min = 1, max = 2000, message = "content phải có độ dài từ 1 đến 2000 ký tự")
    @Schema(
        description = "Nội dung của bình luận",
        example = "Bài viết này rất hay và hữu ích!",
        minLength = 1,
        maxLength = 4000
    )
    String content,
    
    @NotBlank(message = "postId không được phép null")
    @Schema(
        description = "ID của bài viết mà bình luận thuộc về",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    String postId) {
}
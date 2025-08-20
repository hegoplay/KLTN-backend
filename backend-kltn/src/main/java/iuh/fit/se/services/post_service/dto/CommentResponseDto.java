package iuh.fit.se.services.post_service.dto;

import java.time.LocalDateTime;

import iuh.fit.se.services.user_service.dto.ShortUserInfoResponseDto;
import lombok.experimental.FieldDefaults;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CommentResponseDto {
	String commentId;
	String content;
	ShortUserInfoResponseDto commenter;
	LocalDateTime commentDate;
}

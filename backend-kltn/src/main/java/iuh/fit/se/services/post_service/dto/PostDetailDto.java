package iuh.fit.se.services.post_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.user_service.dto.AttachmentDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.experimental.FieldDefaults;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostDetailDto {
	String id;
	String title;
	UserShortInfoResponseDto writer;
	LocalDateTime postTime;
	String content;
	AttachmentDto featureImage;
	FunctionStatus status;
	List<CommentResponseDto> comments;
	LocalDateTime lastModifiedTime;
	
}

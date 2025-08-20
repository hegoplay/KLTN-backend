package iuh.fit.se.services.post_service.dto;

import java.time.LocalDateTime;

import iuh.fit.se.services.user_service.dto.AttachmentDto;
import iuh.fit.se.services.user_service.dto.ShortUserInfoResponseDto;
import lombok.experimental.FieldDefaults;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostWrapperDto {
	String postId;
	String title;
	ShortUserInfoResponseDto writer;
	LocalDateTime postDate;
	String content;
	AttachmentDto featureImage;
	LocalDateTime lastModifiedDate;
	
}

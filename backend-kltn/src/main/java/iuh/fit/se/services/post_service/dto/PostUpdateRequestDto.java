package iuh.fit.se.services.post_service.dto;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.user_service.dto.AttachmentDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostUpdateRequestDto(
	@NotBlank(message = "postId không được phép null") String postId,
	@NotBlank(message = "title không được phép null") String title,
	@NotBlank(message = "content không được phép trống") String content,
	AttachmentDto featureImage,
	@NotNull(message = "status không được phép null") FunctionStatus status
) {

}

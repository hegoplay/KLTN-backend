package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.FunctionStatus;

@Schema(
	description = "DTO dùng để cập nhật trạng thái sự kiện, thường được dùng bởi leader hoặc admin")
public record EventStatusUpdateRequestDto(
	
	@Schema(
		description = "Trạng thái mới của sự kiện (có thể ACCEPTED hoặc REJECTED)",
		example = "REJECTED",
		requiredMode = Schema.RequiredMode.REQUIRED)
	FunctionStatus status) {

}

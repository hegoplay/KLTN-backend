package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO dùng để kích hoạt thủ công danh sách những người tham dự sự kiện")
public record ManualTriggerRequestDto(
	@Schema(
		description = "Danh sách mã định danh của người tham dự",
		example = "[\"uuid user 1\", \"uuid user 2\"]",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<String> attendeeIds) {

}

package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO dùng để tạo biến lưu danh sách thông tin cần chỉnh sửa của người tổ chức sự kiện")
public record ListEventOrganizerRequestDto(
	@Schema(
		description = "Danh sách người tổ chức sự kiện",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<EventOrganizerSingleRequestDto> organizers
) {

}

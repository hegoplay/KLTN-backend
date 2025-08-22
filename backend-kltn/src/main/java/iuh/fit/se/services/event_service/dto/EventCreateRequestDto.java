package iuh.fit.se.services.event_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventCreateRequestDto(
	@Schema(
		description = "Tiêu đề sự kiện",
		example = "Hội thảo về Công nghệ Thông tin 2024",
		requiredMode = RequiredMode.REQUIRED
	) @NotBlank(message = "Tiêu đề không được để trống") String title,
	@Schema(
		description = "Nội dung chi tiết về sự kiện",
		example = "Sự kiện này sẽ tập trung vào các xu hướng mới nhất trong lĩnh vực Công nghệ Thông tin...",
		requiredMode = RequiredMode.REQUIRED
	) @NotBlank(message = "Nội dung không được để trống") String content,

	@NotNull(message = "Địa điểm tổ chức không được null") LocationDto location,
	@Min(1) Integer multiple,
	@Schema(
		description = "Trạng thái lúc khởi tạo sự kiện (Chỉ được là PENDING hoặc ARCHIVED)",
		example = "PENDING",
		requiredMode = RequiredMode.REQUIRED
	) FunctionStatus status,
	Boolean ableToRegister, // phục vụ cho contest
	@NotNull(
		message = "Danh sách người tổ chức không được để trống"
	) List<EventRequestOrganizerDto> organizers,
	String trainingId, // phục vụ cho training event
	@Schema(description = "Loại sự kiện", defaultValue = "SEMINAR")
	@NotNull(
		message = "Loại sự kiện không được để trống"
	) EventCategory category
) {
	@Schema(hidden = true)
	public boolean isCreateAble() {
		return this.status() == null || this.status() == FunctionStatus.PENDING
			|| this.status() == FunctionStatus.ARCHIVED;
	}
}

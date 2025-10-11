package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "DTO dùng để tạo mới sự kiện")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEventCreateRequestDto {
	@Schema(
		description = "Tiêu đề sự kiện",
		example = "Hội thảo về Công nghệ Thông tin 2024",
		requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "Tiêu đề không được để trống")
	private String title;

	@Schema(
		description = "Nội dung chi tiết về sự kiện",
		example = "Sự kiện này sẽ tập trung vào các xu hướng mới nhất trong lĩnh vực Công nghệ Thông tin...",
		requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "Nội dung không được để trống")
	private String description;

	@NotNull(message = "Địa điểm tổ chức không được null")
	private LocationDto location;

	@Schema(
		description = "Hệ số nhân điểm cho sự kiện (ít nhất là 1)",
		example = "1",
		requiredMode = RequiredMode.REQUIRED)
	@Min(1)
	private Integer multiple;

	@NotNull(message = "Danh sách người tổ chức không được để trống")
	private List<EventRequestOrganizerDto> organizers;
}
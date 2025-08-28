package iuh.fit.se.services.event_service.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.validator.TimeBeforeToday;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO dùng để lưu thông tin địa điểm và thời gian của sự kiện")
public class LocationDto {
	@NotBlank(message = "destination must not be blank")
	@Schema(
		description = "Địa điểm của sự kiện",
		example = "Hội trường A, Tòa nhà B, Đại học XYZ"
	)
	String destination;
	@TimeBeforeToday(message = "startTime must be after current time")
	@Schema(
		description = "Thời gian bắt đầu sự kiện",
		example = "2039-12-31T09:00:00"
	)
	LocalDateTime startTime;
	@TimeBeforeToday(message = "endTime must be after current time")
	@Schema(
		description = "Thời gian kết thúc sự kiện",
		example = "2040-12-31T09:00:00"
	)
	LocalDateTime endTime;
}

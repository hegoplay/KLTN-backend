package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO dùng để tự checkin sự kiện")
public record CheckInRequestDto(
	@Schema(
		description = "Mã xác nhận sự kiện và chứa 6 chữ số",
		example = "999999",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank
	String code
	) {

}

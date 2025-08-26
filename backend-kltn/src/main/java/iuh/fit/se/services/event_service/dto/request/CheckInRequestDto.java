package iuh.fit.se.services.event_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequestDto(
	@NotBlank
	String code
	) {

}

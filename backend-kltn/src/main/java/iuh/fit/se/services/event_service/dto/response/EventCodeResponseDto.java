package iuh.fit.se.services.event_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO dùng để trả về mã code của sự kiện")
public record EventCodeResponseDto(String code) {
	
}

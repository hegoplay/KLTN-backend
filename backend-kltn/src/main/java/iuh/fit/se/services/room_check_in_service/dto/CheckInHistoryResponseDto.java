package iuh.fit.se.services.room_check_in_service.dto;

import java.time.LocalDateTime;

public record CheckInHistoryResponseDto(
	LocalDateTime startTime, LocalDateTime endTime
) {

}

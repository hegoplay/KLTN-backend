package iuh.fit.se.services.room_check_in_service.dto;

public record CheckInResponseDto(
//	true là đang check-in, false là đã check-out
	Boolean currentStatus,
	String message
	) {

}

package iuh.fit.se.services.room_check_in_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.se.services.room_check_in_service.dto.CheckInHistoryResponseDto;

public interface RoomCheckInService {

	boolean toggleCheckIn(String userId);
	void checkIn(String userId);
	void checkOut(String userId);
	boolean isCheckedIn(String userId);

	Page<CheckInHistoryResponseDto> getCheckInHistory(
		String userId,
		Pageable pageable
	);

}

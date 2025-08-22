package iuh.fit.se.services.room_check_in_service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import iuh.fit.se.aop.OperatingHoursCheck;
import iuh.fit.se.constant.TimeConstant;
import iuh.fit.se.entity.RoomCheckIn;
import iuh.fit.se.entity.User;
import iuh.fit.se.services.room_check_in_service.dto.CheckInHistoryResponseDto;
import iuh.fit.se.services.room_check_in_service.mapper.RoomCheckInMapper;
import iuh.fit.se.services.room_check_in_service.repository.RoomCheckInRepository;
import iuh.fit.se.services.room_check_in_service.service.RoomCheckInService;
import iuh.fit.se.services.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class RoomCheckInImpl implements RoomCheckInService {

	RoomCheckInRepository roomRepo;
	UserRepository userRepo;
	RoomCheckInMapper roomCheckInMapper;

	@OperatingHoursCheck
	@Override
//	@Transactional
	public void checkIn(String userId) {
		Optional<RoomCheckIn> checkIn = getLastCheckInByUserId(userId);
		if (checkIn.isEmpty() || checkIn.get()
			.getEndTime()
			.isBefore(TimeConstant.getCurrentCloseTime())) {
			User user = userRepo
				.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException(
					"User not found with ID: " + userId));
			RoomCheckIn roomCheckIn = RoomCheckIn
				.builder()
				.user(user)
				.startTime(LocalDateTime.now())
				.build();
			roomRepo.save(roomCheckIn);
		} else {
			throw new IllegalArgumentException("User is already checked in");
		}

	}

	@OperatingHoursCheck
	@Override
//	@Transactional
	public void checkOut(String userId) {
		Optional<RoomCheckIn> checkIn = getLastCheckInByUserId(userId);
		log.info("Check out for user: {}", userId);
		if (!checkIn.isEmpty() && !checkIn.get()
			.getEndTime()
			.isBefore(TimeConstant.getCurrentCloseTime())) {
			RoomCheckIn roomCheckIn = checkIn.get();
			roomCheckIn.setEndTime(LocalDateTime.now());
			roomRepo.save(roomCheckIn);
		} else {
			throw new IllegalArgumentException("User is already checked in");
		}
	}

	@Override
	@OperatingHoursCheck
	public boolean isCheckedIn(String userId) {
		Optional<RoomCheckIn> checkIn = getLastCheckInByUserId(userId);

		if (checkIn.isEmpty()) {
			return false;
		}
		RoomCheckIn lastCheckIn = checkIn.get();
		log.info("get last RoomCheckIn: {}", lastCheckIn);
		boolean res = lastCheckIn.getEndTime() == null
			|| !lastCheckIn.getEndTime().isBefore(TimeConstant.getCurrentCloseTime());
		log.info("isCheckedIn: {}", res);
		return res;
	}

	@Override
	@OperatingHoursCheck
	public boolean toggleCheckIn(String userId) {
		// TODO Auto-generated method stub
		if (isCheckedIn(userId)) {
			checkOut(userId);
			return false;
		} else {
			checkIn(userId);
			return true;
		}
	}

	@Override
	public Page<CheckInHistoryResponseDto> getCheckInHistory(
		String userId,
		Pageable pageable
	) {
		return roomRepo
			.findByUser_Id(userId, pageable)
			.map(roomCheckInMapper::toCheckInResponseDto);
	}

	private Optional<RoomCheckIn> getLastCheckInByUserId(
		String userId
	) {
		return roomRepo
			.findFirstByUser_IdAndStartTimeAfterOrderByStartTimeDesc(userId,
				TimeConstant.fromLocalDate(LocalDate.now()));
	}
	
}

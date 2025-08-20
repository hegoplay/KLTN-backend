package iuh.fit.se.services.room_check_in_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import iuh.fit.se.services.room_check_in_service.dto.CheckInHistoryResponseDto;
import iuh.fit.se.services.room_check_in_service.dto.CheckInResponseDto;
import iuh.fit.se.services.room_check_in_service.service.RoomCheckInService;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/api/room-check-in")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoomCheckInController {

	JwtTokenUtil jwtTokenUtil;
	RoomCheckInService roomCheckInService;
	PagedResourcesAssembler<CheckInHistoryResponseDto> pagedResourcesAssembler;
	UserService userService;

	@PostMapping("toggle")
	public ResponseEntity<CheckInResponseDto> toggleCheckIn(
		HttpServletRequest request
	) {

		String token = jwtTokenUtil.getTokenFromRequest(request);
		// jwt đã được validate ở filter, nên không cần validate lại

		String userId = jwtTokenUtil.getUserIdFromToken(token);

		Boolean toggleCheckIn = roomCheckInService.toggleCheckIn(userId);

		return ResponseEntity
			.ok(new CheckInResponseDto(toggleCheckIn,
				toggleCheckIn
					? "Điểm danh thành công"
					: "Thoát khỏi phòng thành công"));
	}

	@GetMapping("current-status")
	public ResponseEntity<CheckInResponseDto> getCurrentStatus(
		HttpServletRequest request
	) {
		String token = jwtTokenUtil.getTokenFromRequest(request);
		String userId = jwtTokenUtil.getUserIdFromToken(token);

		userService.getUserById(userId);

		boolean isCheckedIn = roomCheckInService.isCheckedIn(userId);

		return ResponseEntity
			.ok(new CheckInResponseDto(isCheckedIn,
				isCheckedIn ? "Đang ở trong phòng" : "Đã thoát khỏi phòng"));
	}
	@GetMapping("check-in-history")
	public ResponseEntity<PagedModel<EntityModel<CheckInHistoryResponseDto>>> getCheckInHistory(
		HttpServletRequest request,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "desc") String sort
	) {
		String token = jwtTokenUtil.getTokenFromRequest(request);
		String userId = jwtTokenUtil.getUserIdFromToken(token);

		Pageable pageable;

		if (sort.equals("asc")) {
			pageable = PageRequest
				.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));
		} else if (sort.equals("desc")) {
			pageable = PageRequest
				.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
		} else {
			return ResponseEntity.badRequest().build(); // Invalid sort
														// parameter
		}
		Page<CheckInHistoryResponseDto> checkInHistory = roomCheckInService
			.getCheckInHistory(userId, pageable);
		return ResponseEntity
			.ok(pagedResourcesAssembler.toModel(checkInHistory));
	}
}

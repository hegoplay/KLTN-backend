package iuh.fit.se.services.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/leader/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(
	name = "User Leader Management",
	description = "API này hỗ trợ các công việc quản lý người dùng của leader."
)
@SecurityRequirement(name = "bearerAuth")
public class UserLeaderController {

	UserService userService;

	@PostMapping("/reset-attendance-point")
	@Operation(summary = "Reset attendance point for all users")
	public ResponseEntity<Void> resetAttendancePointForAllUsers() {
		userService.resetAllAttendancePoint();
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/reset-contribution-point")
	@Operation(summary = "Reset contribution point for all users")
	public ResponseEntity<Void> resetContributionPointForAllUsers() {
		userService.resetAllContributionPoint();
		return ResponseEntity.ok().build();
	}

}

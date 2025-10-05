package iuh.fit.se.services.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import iuh.fit.se.api.UserAPI;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.services.user_service.dto.UserChangeRoleRequestDto;
import iuh.fit.se.services.user_service.dto.UserUpdateInfoRequestDto;
import iuh.fit.se.services.user_service.dto.UserUpdatePasswordDto;
import iuh.fit.se.services.user_service.service.UserService;
import jakarta.validation.Valid;
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
	
	@PutMapping(UserAPI.USER_ID)
	public ResponseEntity<Void> updateUser(
		@PathVariable String userId,
		@RequestBody @Valid UserUpdateInfoRequestDto requestDto
		) {
		userService.updateUserInfo(userId, requestDto);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(UserAPI.USER_ID_RESET_PASSWORD)
	public ResponseEntity<Void> updateUserPassword(
		@PathVariable String userId,
		UserUpdatePasswordDto requestDto
		) {
		userService.leaderUpdateUserPassword(userId, requestDto);
		return ResponseEntity.ok().build();
	}
	@PutMapping(UserAPI.USER_ID_CHANGE_ROLE)
	@Operation(summary = "Change user role. Only leader can do this action.", description = """
		API này cho phép leader thay đổi vai trò của người dùng.
		- Nếu vai trò mới là LEADER và hệ thống đã có leader, cần đặt accepted=true để chấp nhận thay thế.
		- Nếu vai trò mới là ADMIN, không thể thay đổi
		- Các vai trò khác có thể thay đổi bình thường.
		""")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "202",
			description = "Accepted. The role change request has been accepted and is being processed."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "Bad Request. The request is invalid or cannot be processed.",
			content = {}),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "Not Found. The user with the specified ID does not exist.",
			content = {}),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409",
			description = "Conflict. There is a conflict with the current state of the resource, such as exceeding the maximum number of leaders allowed.",
			content = {})
	})
	public ResponseEntity<Void> changeUserRole(
		@PathVariable String userId,
		@RequestBody UserChangeRoleRequestDto dto
		) {
		userService.leaderChangeUserRole(userId, dto);
		return ResponseEntity.accepted().build();
	}

}

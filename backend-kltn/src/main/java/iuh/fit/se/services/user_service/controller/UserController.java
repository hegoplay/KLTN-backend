package iuh.fit.se.services.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.UserInfoResponseDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "User Management", description = "API này hỗ trợ các chức năng quản lý người dùng, bao gồm lấy thông tin người dùng hiện tại.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	UserService userService;
	UserMapper userMapper;

	@GetMapping("/demo")
	public ResponseEntity<String> getDemoUser() {
		return ResponseEntity.ok("admin/admin");
	}
	@GetMapping("/me")
	@Operation(summary = "Lấy thông tin người dùng hiện tại", description = "API này trả về thông tin của người dùng hiện tại dựa trên JWT token trong request.")
	public ResponseEntity<UserInfoResponseDto> getCurrentUser(
		HttpServletRequest request
	) {
		User user = userService.getCurrentUser();

		UserInfoResponseDto userInfo = userMapper.toUserInfoResponseDto(user);

		return ResponseEntity.ok(userInfo);
	}
	
	@GetMapping("/{keyword}")
	public ResponseEntity<UserInfoResponseDto> getUserByKeyword(
		String keyword
	) {
		User user = userService.getUserByKeyword(keyword);

		UserInfoResponseDto userInfo = userMapper.toUserInfoResponseDto(user);

		return ResponseEntity.ok(userInfo);
	}

}

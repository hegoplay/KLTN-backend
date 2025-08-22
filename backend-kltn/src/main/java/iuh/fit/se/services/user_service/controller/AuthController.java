package iuh.fit.se.services.user_service.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.LoginRequestDto;
import iuh.fit.se.services.user_service.dto.LoginResponseDto;
import iuh.fit.se.services.user_service.dto.LogoutResponseDto;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.services.user_service.serviceImpl.TokenBlacklistService;
import iuh.fit.se.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(name = "Auth Management", description = """
		API nãy hỗ trợ các liên quan đến xác thực người dùng, bao gồm đăng ký, đăng nhập và đăng xuất.
	""")

public class AuthController {

	UserService userService;
	JwtTokenUtil jwtTokenUtil;
	org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
	TokenBlacklistService tokenBlacklistService;

	@PostMapping("/register")
	@io.swagger.v3.oas.annotations.Operation(summary = "User registration", description = """
			Đăng ký người dùng mới với tên đăng nhập, email và mật khẩu.
			Mật khẩu sẽ được mã hóa trước khi lưu trữ.
		""")
	public ResponseEntity<?> registerUser(
		@RequestBody @Validated RegisterRequestDto request
	) {
		log.info("Registering user: {}", request.username());

		userService.registerUser(request);
		return ResponseEntity.ok("User registered successfully");

	}

	@PostMapping("/login")
	@io.swagger.v3.oas.annotations.Operation(summary = "User login", description = """
			Đăng nhập người dùng với tên đăng nhập hoặc email và mật khẩu.
			Nếu đăng nhập thành công, trả về JWT token.
			Tài khoản admin: admin/admin
		""")
	public ResponseEntity<LoginResponseDto> login(
		@RequestBody @Validated LoginRequestDto request
	) {
		User user = userService.loadUserByUsernameOrEmail(request.username());

		log.info("User {} is attempting to log in", request.username());
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BadCredentialsException("Invalid password");
		}

		String token = jwtTokenUtil.generateToken(user);

		LoginResponseDto jwtResponse = new LoginResponseDto(token,
			user.getId(), user.getUsername(), user.getEmail(),
			user.getRole());

		return ResponseEntity.ok(jwtResponse);
	}
	@PostMapping("/logout")
	@io.swagger.v3.oas.annotations.Operation(summary = "User logout", description = """
			Đăng xuất người dùng bằng cách thu hồi JWT token.
			Token sẽ được thêm vào danh sách đen để không thể sử dụng lại.
		""")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - No token provided")})
	public ResponseEntity<LogoutResponseDto> logout(
		HttpServletRequest request
	) {
		String token = jwtTokenUtil.getTokenFromRequest(request);

		if (token != null && !token.isEmpty()) {
			log.info("Logging out user with token: {}", token);
			tokenBlacklistService
				.blacklistToken(token,
					jwtTokenUtil.getMilliValidationLeft(token),
					TimeUnit.MILLISECONDS);
			return ResponseEntity
				.ok(new LogoutResponseDto("Logged out successfully"));
		} else {
			log.warn("Logout attempt with no token provided");
			return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new LogoutResponseDto("No token provided"));
		}
	}
	@GetMapping("/test")
	@io.swagger.v3.oas.annotations.Operation(summary = "Test endpoint", description = """
			Endpoint để kiểm tra kết nối và xác thực.
			Nếu thành công, trả về thông báo "Test successful".
		""")
	public ResponseEntity<String> test() {
		return new ResponseEntity<>("Test successful", HttpStatus.OK);
	}
}

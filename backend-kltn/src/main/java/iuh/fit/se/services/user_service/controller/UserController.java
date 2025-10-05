package iuh.fit.se.services.user_service.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.api.UserAPI;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.services.user_service.dto.UpdatePasswordRequestDto;
import iuh.fit.se.services.user_service.dto.UserInfoResponseDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import iuh.fit.se.services.user_service.dto.UserUpdateInfoRequestDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.JwtTokenUtil;
import iuh.fit.se.util.PageableUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(UserAPI.BASE_URL)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = """
	API này hỗ trợ các chức năng quản lý người dùng,
	bao gồm lấy thông tin người dùng hiện tại.
	""")
@ApiResponses(
	value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "Thành công"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "Yêu cầu không hợp lệ"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "Chưa xác thực",
			content = {}),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "Không có quyền truy cập",
			content = {}),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "Không tìm thấy"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "Lỗi máy chủ")
	}
)
public class UserController {

	UserService userService;
	UserMapper userMapper;
	JwtTokenUtil jwtT;
	PagedResourcesAssembler<UserShortInfoResponseDto> pagedResourcesAssembler;

	@GetMapping("/demo")
	public ResponseEntity<String> getDemoUser() {
		return ResponseEntity.ok("admin/admin");
	}

	@Operation(
		summary = "Lấy thông tin người dùng hiện tại",
		description = "API này trả về thông tin của người dùng hiện tại dựa trên JWT token trong request.")
	@GetMapping("/me")
	public ResponseEntity<UserInfoResponseDto> getCurrentUser(
		HttpServletRequest request) {
		User user = userService.getCurrentUser();

		UserInfoResponseDto userInfo = userMapper.toUserInfoResponseDto(user);

		return ResponseEntity.ok(userInfo);
	}

	@Operation(
		summary = "Tìm kiếm người dùng bằng từ khóa",
		description = """
			API này cho phép tìm kiếm người dùng dựa trên từ khóa có thể là username, email hoặc số điện thoại.
			Từ khóa được truyền dưới dạng tham số trong URL.
			""")
	@GetMapping("/{keyword}")
	public ResponseEntity<UserInfoResponseDto> getUserByKeyword(
		String keyword) {
		User user = userService.getUserByKeyword(keyword);

		UserInfoResponseDto userInfo = userMapper.toUserInfoResponseDto(user);

		return ResponseEntity.ok(userInfo);
	}

	@GetMapping("/search")
	@Operation(
		summary = "Tìm kiếm người dùng",
		description = """
			API này cho phép tìm kiếm người dùng dựa trên từ khóa có thể là username, email hoặc số điện thoại.
			Từ khóa được truyền dưới dạng tham số trong URL.
			Kết quả trả về là danh sách người dùng được phân trang.
			""")
	public ResponseEntity<PagedModel<EntityModel<UserShortInfoResponseDto>>> searchUsers(
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "username,asc") String sort,
		@RequestParam(required = false) UserRole role) {
		var pageSort = PageableUtil.parseSort(sort);
		var users = userService
			.searchUsers(keyword, PageRequest.of(page, size, pageSort), role);
		var userDtos = users.map(userMapper::toShortUserInfoResponseDto);
		return ResponseEntity.ok(pagedResourcesAssembler.toModel(userDtos));
	}

	@PutMapping(UserAPI.UPDATE_MY_INFO)
	@Operation(
		summary = "Cập nhật thông tin người dùng hiện tại",
		description = """
			API này cho phép người dùng cập nhật thông tin cá nhân của họ.
			Thông tin được truyền dưới dạng JSON trong body của request.
			""")
	public ResponseEntity<UserInfoResponseDto> updateUserInfo(
		@Valid @RequestBody UserUpdateInfoRequestDto dto,
		HttpServletRequest httpServletRequest
		) {
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		User updatedUser = userService
			.updateUserInfo(userId, dto);
		return ResponseEntity.ok(userMapper.toUserInfoResponseDto(updatedUser));
	}
	
	@PutMapping(UserAPI.UPDATE_MY_PASSWORD)
	public ResponseEntity<Void> updateUserInfoById(
		@Valid @RequestBody UpdatePasswordRequestDto dto,
		HttpServletRequest httpServletRequest
		) {
		String userId = jwtT.getUserIdFromRequest(httpServletRequest);
		userService.updateMyPassword(userId, dto);
		return ResponseEntity.ok().build();
	}
}

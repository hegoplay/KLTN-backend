package iuh.fit.se.services.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@Schema(
	description = """
		DTO dùng để trả về thông tin ngắn gọn của người dùng
		Hạn chế lộ thông tin nhạy cảm như mật khẩu, vai trò, trạng thái tài khoản,...
		""")
@EqualsAndHashCode
@NoArgsConstructor
public class UserShortInfoResponseDto {
	@EqualsAndHashCode.Include
	@Schema(
		description = "Mã định danh của người dùng",
		example = "550e8400-e29b-41d4-a716-446655440000",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String id;
	@Schema(
		description = "Tên đăng nhập của người dùng",
		example = "john_doe",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String username;
	@Schema(
		description = "Địa chỉ email của người dùng",
		example = "aaaaaaa@gmail.com",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String email;
	@Schema(
		description = "Số điện thoại của người dùng",
		example = "+84123456789",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String nickname;
	@Schema(
		description = "URL ảnh đại diện của người dùng",
		example = "https://example.com/avatar.jpg",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String userUrl;
	@Schema(
		description = "Họ và tên đầy đủ của người dùng",
		example = "John Doe",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String fullName;
	UserRole role;
	Integer attendancePoint;
	Integer contributionPoint;
	Boolean disabled;
}

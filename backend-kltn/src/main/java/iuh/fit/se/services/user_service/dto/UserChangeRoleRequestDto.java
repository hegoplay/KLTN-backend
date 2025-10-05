package iuh.fit.se.services.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.FieldDefaults;

@lombok.Value
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(
	description = """
		DTO dùng để gửi yêu cầu thay đổi vai trò người dùng.
		""")
public class UserChangeRoleRequestDto {
	@NotNull(message = "Vai trò mới không được để trống")
	UserRole newRole;
	@Schema(description = "Chấp nhận thay đổi vai trò hay không. Tương đối giống -f trong linux. Nếu chấp nhận sẽ thay thế vai trò leader đã có trong hệ thống")
	boolean accepted;
	
}

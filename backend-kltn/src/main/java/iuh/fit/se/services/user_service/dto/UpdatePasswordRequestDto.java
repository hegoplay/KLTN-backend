package iuh.fit.se.services.user_service.dto;

import iuh.fit.se.validator.CustomPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UpdatePasswordRequestDto {
	@NotNull(message = "Mật khẩu cũ không được để trống")
	String oldPassword;
	@NotNull(message = "Mật khẩu mới không được để trống")
	@CustomPassword(
		message = "Mật khẩu mới phải chứa ít nhất 1 chữ cái viết hoa hoặc chữ số và có độ dài từ 8 đến 32 ký tự")
	String newPassword;
	@NotNull(message = "Xác nhận mật khẩu mới không được để trống")
	String confirmNewPassword;
}

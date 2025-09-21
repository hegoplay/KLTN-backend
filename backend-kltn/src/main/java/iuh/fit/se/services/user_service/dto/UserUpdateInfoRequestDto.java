package iuh.fit.se.services.user_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class UserUpdateInfoRequestDto {
	@Email(message = "Email không hợp lệ")
	String email;
	String nickname;
	LocalDate dateOfBirth;
	@NotNull(message = "Họ và tên không được để trống")
	String fullName;
}

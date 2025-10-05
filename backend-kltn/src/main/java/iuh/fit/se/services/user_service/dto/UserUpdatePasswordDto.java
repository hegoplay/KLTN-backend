package iuh.fit.se.services.user_service.dto;


import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class UserUpdatePasswordDto {
	String newPassword;
}

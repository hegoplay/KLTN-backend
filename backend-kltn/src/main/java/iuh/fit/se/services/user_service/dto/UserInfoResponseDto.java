package iuh.fit.se.services.user_service.dto;

import java.util.Date;

import iuh.fit.se.entity.enumerator.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class UserInfoResponseDto {
	String id;
	String username;
	String email;
	String nickname;
	Date dateOfBirth;
	UserRole role;
	Integer attendancePoint;
	Integer contributionPoint;
	Boolean disabled;
}

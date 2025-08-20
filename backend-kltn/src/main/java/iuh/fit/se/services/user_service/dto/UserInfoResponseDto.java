package iuh.fit.se.services.user_service.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
	String userId;
	String username;
	String email;
	String nickname;
	Date dateOfBirth;
	UserRole role;
	Integer attendancePoint;
	Integer contributePoint;
	Boolean disabled;
	LocalDateTime lastResetAttendancePoint;
	LocalDateTime lastResetContributePoint;
}

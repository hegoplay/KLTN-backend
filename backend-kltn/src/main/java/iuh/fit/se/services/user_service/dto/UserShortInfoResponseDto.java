package iuh.fit.se.services.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class UserShortInfoResponseDto {
	String id;
	String username;
	String email;
	String nickname;
	String userUrl;
	String fullName;
}

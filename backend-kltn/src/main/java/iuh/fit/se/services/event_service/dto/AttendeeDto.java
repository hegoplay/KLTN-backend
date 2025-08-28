package iuh.fit.se.services.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để trả về thông tin người tham dự sự kiện")
public class AttendeeDto {
	UserShortInfoResponseDto user;
	AttendeeStatus status;
}

package iuh.fit.se.services.event_service.dto;

import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ExamResultDto {
	String id;
	UserShortInfoResponseDto dto;
	Integer point;
	Integer rank;
}

package iuh.fit.se.services.event_service.dto;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventTimeStatus;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;

@lombok.Builder
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventWrapperDto {
	String id;
	UserShortInfoResponseDto host;
	LocationDto location;
	String title;
	String content;
	Integer multiple;
	FunctionStatus status;
	Boolean isDone;
	EventTimeStatus timeStatus;
}

package iuh.fit.se.services.training_service.dto;

import java.util.List;

import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@SuperBuilder
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class TrainingDetailDto extends TrainingWrapperDto {
	String description;
	List<EventWrapperDto> trainingEvents;
	List<UserShortInfoResponseDto> mentors;
	
}

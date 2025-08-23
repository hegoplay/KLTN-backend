package iuh.fit.se.services.event_service.patterns;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TrainingEventFactory extends GenerateEventFactory {

	EventMapper eventMapper;

	@Override
	protected Event generateEvent(EventCreateRequestDto dto) {
		TrainingEvent seminar = eventMapper.toTrainingEvent(dto);
		return seminar;
	}

	@Override
	public EventDetailResponseDto toEventDetailResponseDto(Event e) {

		var dto = eventMapper.toEventDetailResponseDto((TrainingEvent) e);
		dto
			.setCategory(
				iuh.fit.se.services.event_service.dto.enumerator.EventCategory.TRAINING_EVENT);
		return dto;
	}

}

package iuh.fit.se.services.event_service.patterns.factoryPattern;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeminarFactory extends GenerateEventFactory {

	EventMapper eventMapper;

	@Override
	protected Event generateEvent(EventCreateRequestDto dto) {
		log.info("Generated seminar: {}", dto);
		Seminar seminar = eventMapper.toSeminar(dto);
		return seminar;
	}

	@Override
	public EventDetailResponseDto toEventDetailResponseDto(Event e) {

		EventDetailResponseDto dto = eventMapper
			.toEventDetailResponseDto((Seminar) e);
		dto
			.setCategory(
				iuh.fit.se.services.event_service.dto.enumerator.EventCategory.SEMINAR);
		return dto;
	}

}

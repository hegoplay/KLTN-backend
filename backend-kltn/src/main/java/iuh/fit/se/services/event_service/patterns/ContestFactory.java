package iuh.fit.se.services.event_service.patterns;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ContestFactory extends GenerateEventFactory {

	EventMapper eventMapper;

	@Override
	protected Event generateEvent(EventCreateRequestDto dto) {
		Contest contest = eventMapper.toContest(dto);
		contest.setAbleToRegister(true);
		return contest;
	}

	@Override
	public EventDetailResponseDto toEventDetailResponseDto(Event e) {
		var dto = eventMapper.toEventDetailResponseDto((Contest) e);
		dto.setCategory(EventCategory.CONTEST);
		return dto;
	}

}

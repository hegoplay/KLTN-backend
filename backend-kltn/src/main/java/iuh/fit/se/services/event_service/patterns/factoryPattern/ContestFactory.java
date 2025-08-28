package iuh.fit.se.services.event_service.patterns.factoryPattern;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
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

	@Override
	protected Event handleUpdateEvent(Event e, EventUpdateRequestDto dto) {
		if (!(e instanceof Contest contest)) {
			throw new IllegalArgumentException("Event is not a Contest");
		}
		eventMapper.updateEventFromDto(dto, contest);
		if (contest.getLocation() != null) {
			if (contest
				.getLocation()
				.getEndTime()
				.isBefore(contest.getLocation().getStartTime())) {
				throw new IllegalArgumentException(
					"End time must be after start time");
			}
			if (contest
				.getLocation()
				.getStartTime()
				.isBefore(java.time.LocalDateTime.now())) {
				throw new IllegalArgumentException(
					"Start time must be in the future");
			}
		}

		return contest;
	}

}

package iuh.fit.se.services.event_service.patterns.factoryPattern;


import java.util.HashSet;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ClosedContestFactory extends EventFactory {

	EventMapper eventMapper;

	@Override
	protected Event generateEvent(BaseEventCreateRequestDto dto) {
		if (!(dto instanceof SingleEventCreateRequestDto)) {
			throw new IllegalArgumentException(
				"Invalid DTO type for Closed Contest");
		}
		Contest contest = eventMapper
			.toContestIgnoreOrganizer((SingleEventCreateRequestDto) dto);
		contest.setAbleToRegister(false);
		if (contest.getStatus() == null) {
			contest.setStatus(FunctionStatus.ARCHIVED);
		}
		return contest;
	}

	@Override
	public EventDetailResponseDto toEventDetailResponseDto(Event e) {
		var dto = eventMapper.toEventDetailResponseDto((Contest) e);
		dto.setCategory(EventCategory.CLOSED_CONTEST);

		return dto;
	}

	@Override
	protected Event handleUpdateEvent(Event e, EventUpdateRequestDto dto) {
		Contest contest = (Contest) e;
		eventMapper.updateEventFromDto(dto, contest);
		contest.setAbleToRegister(false);
		return contest;
	}

	@Override
	protected void addNewOrganizerToEvent(
		Event event,
		EventOrganizerSingleRequestDto req,
		User organizerUser
	) {
		EventOrganizer organizer = EventOrganizer
			.builder()
			.event(event)
			.organizer(organizerUser)
			.roles(req.roles() != null
				? new HashSet<>(req.roles())
				: new HashSet<>())
			.roleContent(req.roleContent())
			.build();
		event.addOrganizer(organizer);
	}

	@Override
	public void checkType() {
		// TODO Auto-generated method stub
		log.info("This is Closed Contest Factory");
	}

}

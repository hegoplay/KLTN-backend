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

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ContestFactory extends EventFactory {

	EventMapper eventMapper;

	@Override
	protected Event generateEvent(BaseEventCreateRequestDto dto) {
		if (!(dto instanceof SingleEventCreateRequestDto)) {
			throw new IllegalArgumentException(
				"Invalid DTO type for Closed Contest");
		}
		Contest contest = eventMapper
			.toContest((SingleEventCreateRequestDto) dto);
		contest.setAbleToRegister(true);
		if (contest.getStatus() == null) {
			contest.setStatus(FunctionStatus.ARCHIVED);
		}
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

	@Override
	protected void addNewOrganizerToEvent(
		Event event,
		EventOrganizerSingleRequestDto req,
		User organizerUser
	) {
		EventOrganizer organizer = EventOrganizer
			.builder()
			.organizerId(organizerUser.getId())
			.eventId(event.getId())
			.event(event)
			.organizer(organizerUser)
			.roles(req.roles() != null
				? new HashSet<>(req.roles())
				: new HashSet<>())
			.roleContent(req.roleContent())
			.build();
		event.addOrganizer(organizer);
	}

}

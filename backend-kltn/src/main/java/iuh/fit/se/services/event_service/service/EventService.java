package iuh.fit.se.services.event_service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;

public interface EventService {
	EventDetailResponseDto createEvent(BaseEventCreateRequestDto dto);

	void deleteEvent(String eventId);

	Page<Event> searchPublicEvents(EventSearchRequestDto request);

	Page<Event> searchUserEvents(
		EventSearchRequestDto request,
		String userId,
		FunctionStatus status
	);

	Page<Event> searchAllEvents(
		EventSearchRequestDto request,
		FunctionStatus status
	);

	Page<Event> searchMyEvents(
		EventSearchRequestDto request,
		FunctionStatus status,
		String userId
	);

	EventDetailResponseDto getEventById(String eventId);

	EventDetailResponseDto getEventByIdAndUserId(String eventId, String userId);

	void updateSingleEventStatus(String eventId, FunctionStatus status);
	void updateEventStatusWithoutSaving(Event event, FunctionStatus status);

	void triggerEventDone(String eventId);

	String getEventCode(String eventId);

	void selfTriggerRegisterEvent(String eventId);

	void manualTriggerRegisterEvent(String eventId, List<String> attendeeIds);
	
	void triggerRegisterEvent(Event e, String userId);
	
	Event registerEventWithoutSaving(Event e, String userId);
	
	Event unregisterEventWithoutSaving(Event e, String userId);

	void selfCheckInEvent(String eventId, String userId, String otp);

	void manualCheckInEvent(String eventId, List<String> attendeeIds);

	Attendee checkInEventWithoutSaving(Event event, String userId);
	
	Event updateEventOrganizers(String eventId, List<EventOrganizerSingleRequestDto> organizerRequests);
	
	List<EventOrganizer> getEventOrganizers(String eventId);
	
	void triggerBan(String eventId, List<String> attendeesId, String currentUserId);
	
	EventDetailResponseDto updateEvent(String eventId, EventUpdateRequestDto dto, String currentUserId);
}

package iuh.fit.se.services.event_service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;

public interface EventService {
	EventDetailResponseDto createEvent(EventCreateRequestDto dto);

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
		FunctionStatus status
	);

	EventDetailResponseDto getEventById(String eventId);

	EventDetailResponseDto getEventByIdAndUserId(String eventId, String userId);

	void updateEventStatus(String eventId, FunctionStatus status);

	void triggerEventDone(String eventId);

	String getEventCode(String eventId);

	void selfTriggerRegisterEvent(String eventId);

	void manualTriggerRegisterEvent(String eventId, List<String> attendeeIds);

	void triggerRegisterEvent(Event e, String userId);

	void selfCheckInEvent(String eventId, String userId, String otp);

	void manualCheckInEvent(String eventId, List<String> attendeeIds);

	void checkInEvent(Event event, String userId);
	
}

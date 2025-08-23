package iuh.fit.se.services.event_service.service;

import org.springframework.data.domain.Page;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequest;

public interface EventService {
	EventDetailResponseDto createEvent(EventCreateRequestDto dto);
	
	void deleteEvent(String eventId);
	
	Page<Event> searchPublicEvents(EventSearchRequest request);
	
	Page<Event> searchUserEvents(EventSearchRequest request, String userId, FunctionStatus status);
	
	Page<Event> searchAllEvents(EventSearchRequest request, FunctionStatus status);
	
	Page<Event> searchMyEvents(EventSearchRequest request, FunctionStatus status);
	
	EventDetailResponseDto getEventById(String eventId);
	
	void updateEventStatus(String eventId, FunctionStatus status);
	
	void triggerEventDone(String eventId);
	
	String getEventOTP(String eventId);
}

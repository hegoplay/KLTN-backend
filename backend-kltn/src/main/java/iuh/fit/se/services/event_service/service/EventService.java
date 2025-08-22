package iuh.fit.se.services.event_service.service;

import iuh.fit.se.services.event_service.dto.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;

public interface EventService {
	EventDetailResponseDto createEvent(EventCreateRequestDto dto);
	
}

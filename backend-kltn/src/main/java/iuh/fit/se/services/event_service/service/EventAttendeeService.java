package iuh.fit.se.services.event_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.se.entity.Attendee;

public interface EventAttendeeService {
	Page<Attendee> getAllAttendeesByEventId(String eventId, Pageable pageable);
}

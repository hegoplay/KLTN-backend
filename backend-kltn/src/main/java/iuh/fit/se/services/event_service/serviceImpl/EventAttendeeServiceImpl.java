package iuh.fit.se.services.event_service.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.errorHandler.NotFoundErrorHandler;
import iuh.fit.se.services.event_service.repository.EventAttendeeRepository;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.service.EventAttendeeService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EventAttendeeServiceImpl implements EventAttendeeService{

	EventAttendeeRepository eventAttendeeRepository;
	EventRepository eventRepository;
	
	@Override
	public Page<Attendee> getAllAttendeesByEventId(
		String eventId,
		Pageable pageable
	) {
		
		if (!eventRepository.existsById(eventId)) {
			throw new NotFoundErrorHandler("Event with id " + eventId + " does not exist");
		}
		return eventAttendeeRepository.findByEventId(eventId, pageable);
	}

}

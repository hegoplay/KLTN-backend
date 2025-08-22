package iuh.fit.se.services.event_service.serviceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import iuh.fit.se.entity.Event;
import iuh.fit.se.services.event_service.dto.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.patterns.ContestFactory;
import iuh.fit.se.services.event_service.patterns.GenerateEventFactory;
import iuh.fit.se.services.event_service.patterns.SeminarFactory;
import iuh.fit.se.services.event_service.patterns.TrainingEventFactory;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

	EventMapper eventMapper;
	
	UserService userService;
	
	EventRepository eventRepository;

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	public EventDetailResponseDto createEvent(EventCreateRequestDto dto) {
		log.info("Creating event: {}", dto);
		GenerateEventFactory factory = getFactory(dto);
		Event e = factory.createEvent(dto, userService);
		Event savedEvent = eventRepository.save(e);
		return factory.toEventDetailResponseDto(savedEvent);
	}

	private GenerateEventFactory getFactory(EventCreateRequestDto dto) {
		return switch (dto.category()) {
			case SEMINAR -> new SeminarFactory(eventMapper);
			case CONTEST -> new ContestFactory(eventMapper);
			case TRAINING_EVENT -> new TrainingEventFactory(eventMapper);
			default -> throw new IllegalArgumentException(
				"Unsupported event type: " + dto.category());
		};
	}
}

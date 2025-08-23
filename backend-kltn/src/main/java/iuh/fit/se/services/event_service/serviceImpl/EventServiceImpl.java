package iuh.fit.se.services.event_service.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.GlobalConfiguration;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.repository.GlobalConfigurationRepository;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequest;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.patterns.ContestFactory;
import iuh.fit.se.services.event_service.patterns.GenerateEventFactory;
import iuh.fit.se.services.event_service.patterns.SeminarFactory;
import iuh.fit.se.services.event_service.patterns.TrainingEventFactory;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.services.event_service.specification.EventSpecification;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.ContextUtil;
import jakarta.transaction.Transactional;
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

	UserRepository userRepository;

	GlobalConfigurationRepository globalConfigurationRepository;
	
	

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	public EventDetailResponseDto createEvent(EventCreateRequestDto dto) {
		log.info("Creating event: {}", dto);
		User currentUser = userService.getCurrentUser();

		if (!currentUser.isLeader()
			&& dto.category() == EventCategory.CLOSED_CONTEST) {
			throw new IllegalArgumentException(
				"Only leaders can create closed contests");
		}
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
			case CLOSED_CONTEST -> new ContestFactory(eventMapper);
			// ClosedContestFactory(eventMapper);
			default -> throw new IllegalArgumentException(
				"Unsupported event type: " + dto.category());
		};
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void deleteEvent(String eventId) {
		// ContextUtil.getCurrentUsername();
		if (!ContextUtil.isLeader()) {
			throw new SecurityException("Only the host can delete this event");
		}
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));
		if (event
			.getLocation()
			.getStartTime()
			.isBefore(java.time.LocalDateTime.now())) {
			throw new IllegalStateException(
				"Cannot delete an event that has already started or completed");
		}
		eventRepository.deleteById(eventId);
		log.info("Deleted event with ID: {}", eventId);
	}

	@Override
	public Page<Event> searchPublicEvents(EventSearchRequest request) {
		Specification<Event> spec = Specification.unrestricted();

		spec = spec.and(EventSpecification.hasStatus(FunctionStatus.ACCEPTED));
		spec = spec.and(EventSpecification.exceptOfType(TrainingEvent.class));
		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public Page<Event> searchUserEvents(
		EventSearchRequest request,
		String userId,
		FunctionStatus status
	) {

		Specification<Event> spec = Specification.unrestricted();

		spec = spec.and(EventSpecification.hasHostedUserId(userId));

		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public Page<Event> searchAllEvents(
		EventSearchRequest request,
		FunctionStatus status
	) {
		Specification<Event> spec = Specification.unrestricted();

		// Filter by status
		if (status != null) {
			spec = spec.and(EventSpecification.hasStatus(status));
		}

		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	public Page<Event> searchMyEvents(
		EventSearchRequest request,
		FunctionStatus status
	) {

		String currentUsername = ContextUtil.getCurrentUsername();

		Specification<Event> spec = Specification.unrestricted();

		spec = spec.and(EventSpecification.hasHostedUsername(currentUsername));

		if (status != null) {
			spec = spec.and(EventSpecification.hasStatus(status));
		}

		return searchEvents(spec, request);
	}

	@Override
	public EventDetailResponseDto getEventById(String eventId) {
		return eventMapper
			.toEventDetailResponseDto(eventRepository
				.findById(eventId)
				.orElseThrow(() -> new IllegalArgumentException(
					"Event with ID " + eventId + " does not exist")));
	}

	private Page<Event> searchEvents(
		Specification<Event> spec,
		EventSearchRequest request
	) {

		// Keyword search (title hoặc content)
		if (request.keyword() != null && !request.keyword().isEmpty()) {
			spec = spec
				.and(EventSpecification
					.hasTitleContaining(request.keyword())
					.or(EventSpecification
						.hasContentContaining(request.keyword())));
		}
		// Filter by event type
		if (request.type() != null && request.type() != EventSearchType.ALL) {
			Class<? extends Event> eventClass = switch (request.type()) {
				case SEMINAR -> Seminar.class;
				case CONTEST -> Contest.class;
				// case TRAINING -> TrainingEvent.class;
				default -> null;
			};
			if (eventClass != null) {
				spec = spec.and(EventSpecification.isOfType(eventClass));
			}
			// else {
			// spec =
			// spec.and(EventSpecification.exceptOfType(TrainingEvent.class));
			// }
		}

		// Filter by isDone
		if (request.isDone() != null) {
			if (request.isDone()) {
				spec = spec
					.and((root, query, cb) -> cb.isTrue(root.get("isDone")));
			} else {
				spec = spec.and(EventSpecification.isNotDone());
			}
		}

		return eventRepository.findAll(spec, request.toPageable());
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void updateEventStatus(String eventId, FunctionStatus status) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));
		event.setStatus(status);

	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	@Transactional
	public void triggerEventDone(String eventId) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));
		GlobalConfiguration config = globalConfigurationRepository
			.findByConfigKey(GlobalConfiguration.KEY_LAST_RESET_POINT_TIME)
			.orElseThrow(() -> new IllegalStateException(
				"Global configuration for last reset point time not found"));
		if (event.isDone()) {
			if (event
				.getDoneTime()
				.isBefore(config.getConfigValueAsDateTime())) {
				throw new IllegalStateException(
					"Không thể hoàn tác sự kiện đã hoàn thành trước lần reset điểm gần nhất");
			} else {
				if (event instanceof Contest contest) {
					updateContestUsersScore(contest, -1);
				} else {
					updateEventUsersScore(event, -1);
				}
				event.setDone(false);
			}
		} else {
			event.setDone(true);
			if (event instanceof Contest contest) {
				updateContestUsersScore(contest, 1);
			} else {
				updateEventUsersScore(event, 1);
			}
		}
		event.setDoneTime(java.time.LocalDateTime.now());
		eventRepository.save(event);
	}

	private void updateEventUsersScore(Event event, int base) {
		// handle event check-in user score
		userRepository
			.incrementScoreForEventAttendees(event, base * event.getMultiple());
		userRepository
			.incrementScoreForEventOrganizers(event,
				base * event.getMultiple());
	}

	private void updateContestUsersScore(Contest contest, int base) {
		updateEventUsersScore(contest, base);
		userRepository.incrementScoreForContestWinners(contest, base);
	}

	@Override
	public String getEventOTP(String eventId) {
		// TODO Auto-generated method stub
		return null;
	}
}

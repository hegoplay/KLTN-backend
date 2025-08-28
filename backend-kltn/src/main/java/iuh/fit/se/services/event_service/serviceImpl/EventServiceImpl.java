package iuh.fit.se.services.event_service.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.GlobalConfiguration;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.entity.enumerator.OrganizerRole;
import iuh.fit.se.entity.id_class.AttendeeId;
import iuh.fit.se.entity.id_class.EventOrganizerId;
import iuh.fit.se.errorHandler.NotFoundErrorHandler;
import iuh.fit.se.repository.GlobalConfigurationRepository;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.mapper.EventOrganizerMapper;
import iuh.fit.se.services.event_service.patterns.factoryPattern.ContestFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.GenerateEventFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.SeminarFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.TrainingEventFactory;
import iuh.fit.se.services.event_service.repository.EventAttendeeRepository;
import iuh.fit.se.services.event_service.repository.EventOrganizerRepository;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.service.EventCodeService;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.services.event_service.specification.EventSpecification;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

	EventMapper eventMapper;
	EventOrganizerMapper eventOrganizerMapper;

	UserService userService;

	EventRepository eventRepository;
	UserRepository userRepository;
	EventOrganizerRepository eventOrganizerRepository;
	EventAttendeeRepository eventAttendeeRepository;

	GlobalConfigurationRepository globalConfigurationRepository;

	EventCodeService eventCodeService;

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	@Transactional
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
		EventDetailResponseDto eventDetailResponseDto = factory
			.toEventDetailResponseDto(savedEvent);

		// Attendee attendee =
		// savedEvent.getAttendeeByUserId(currentUser.getId());
		// eventDetailResponseDto.setUserAttendeeStatus(attendee.getStatus());

		return eventDetailResponseDto;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void deleteEvent(String eventId) {
		// ContextUtil.getCurrentUsername();
		if (!ContextUtil.isLeader()) {
			throw new SecurityException("Only leaders can delete events");
		}
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (event.isDone()) {
			throw new IllegalStateException(
				"Không thể xóa sự kiện đã hoàn thành");
		}
		eventRepository.deleteById(eventId);
		log.info("Deleted event with ID: {}", eventId);
	}

	@Override
	public Page<Event> searchPublicEvents(EventSearchRequestDto request) {
		Specification<Event> spec = Specification.unrestricted();

		spec = spec.and(EventSpecification.hasStatus(FunctionStatus.ACCEPTED));
		spec = spec.and(EventSpecification.isSingle(true));
		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public Page<Event> searchUserEvents(
		EventSearchRequestDto request,
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
		EventSearchRequestDto request,
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
		EventSearchRequestDto request,
		FunctionStatus status,
		String userId
	) {

		String currentUsername = ContextUtil.getCurrentUsername();

		Specification<Event> spec = Specification.unrestricted();

		spec = spec
			.and(EventSpecification
				.hasHostedUsername(currentUsername)
				.or(EventSpecification.includeOrganizerId(userId)));

		if (status != null) {
			spec = spec.and(EventSpecification.hasStatus(status));
		}

		return searchEvents(spec, request);
	}

	@Override
	public EventDetailResponseDto getEventById(String eventId) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));

		EventDetailResponseDto dto = eventMapper
			.toEventDetailResponseDto(event);

		return dto;
	}

	@Override
	@Transactional
	public EventDetailResponseDto getEventByIdAndUserId(
		String eventId,
		String userId
	) {

		Event event = eventRepository
			.findByIdAndFetchAttendees(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));

		EventDetailResponseDto dto = eventMapper
			.toEventDetailResponseDto(event);

		User user = userRepository
			.findById(userId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"User with ID " + userId + " does not exist"));

		log.info("Attendee list: {}", event.getAttendeesMap());

		Attendee attendee = event.getAttendeeByUserId(user.getId());
		if (attendee != null) {
			dto.setUserAttendeeStatus(attendee.getStatus());
			EventOrganizer organizer = event.getOrganizerByUserId(user.getId());
			if (organizer != null) {
				dto
					.setUserAsOrganizer(
						eventOrganizerMapper.toEventOrganizerDto(organizer));
			}

		}

		return dto;
	}

	private Page<Event> searchEvents(
		Specification<Event> spec,
		EventSearchRequestDto request
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
//		startTime
		if (request.startTime() != null) {
			spec = spec.and(EventSpecification.hasTimeBetween(request.startTime(),request.endTime()));
		}

		return eventRepository.findAll(spec, request.toPageable());
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void updateEventStatus(String eventId, FunctionStatus status) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));

		if (event.isDone()) {
			throw new IllegalStateException(
				"Cannot change status of an event that is already done");
		}
		// if (event.getStatus() != FunctionStatus.PENDING) {
		// throw new IllegalStateException(
		// "Can only change status of events that are in PENDING state");
		// }
		// if (status != FunctionStatus.ACCEPTED || status !=
		// FunctionStatus.REJECTED) {
		//// TODO: chỉnh lại trạng thái được phép chuyển, hiện tại chỉ rằng buộc
		// 2 trạng thái
		// throw new IllegalArgumentException(
		// "Status must be either ACCEPTED or REJECTED");
		// }
		event.setStatus(status);
		eventRepository.save(event);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	@Transactional
	public void triggerEventDone(String eventId) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		GlobalConfiguration config = globalConfigurationRepository
			.findByConfigKey(GlobalConfiguration.KEY_LAST_RESET_POINT_TIME)
			.orElseThrow(() -> new IllegalStateException(
				"Global configuration for last reset point time not found"));
		if (event.getStatus() != FunctionStatus.ACCEPTED) {
			throw new IllegalStateException(
				"Cannot mark an event as done if it is not in ACCEPTED status");
		}
		// TODO: chỉnh lại thời gian check done
		if (event
			.getLocation()
			.getStartTime()
			.isAfter(java.time.LocalDateTime.now())) {
			throw new IllegalStateException(
				"Cannot mark an event as done before its start time");
		}

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
	@PreAuthorize("hasRole('LEADER') or hasRole('ADMIN') or hasRole('MEMBER')")
	public String getEventCode(String eventId) {
		// TODO: tạo test case
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		// Check if the current user is the host or an attendee
		User currentUser = userService.getCurrentUser();
		// Check if the user is the host, an organizer, or a leader
		if (!checkEventRole(event, currentUser.getId(), OrganizerRole.CODE)) {
			throw new SecurityException(
				"Only the host, organizers with CODE role, or leader can get the event code");
		}
		// kiểm tra xem event đã được duyệt chưa
		if (event.getStatus() != FunctionStatus.ACCEPTED) {
			throw new IllegalStateException(
				"Không thể lấy mã code cho sự kiện chưa được duyệt");
		}
		if (!checkValidTime(event)) {
			throw new IllegalStateException(
				"Không thể phát sinh mã code ngoài thời gian diễn ra sự kiện");
		}

		if (eventCodeService.hasEventCode(eventId)) {
			return eventCodeService.getCurrentEventCode(eventId);
		} else {
			String otp = eventCodeService.generateOrUpdateEventCode(eventId);
			return otp;
		}
	}

	@Override
	public void selfCheckInEvent(String eventId, String userId, String code) {
		if (eventCodeService.verifyEventCode(eventId, code)) {
			Event event = eventRepository
				.findById(eventId)
				.orElseThrow(() -> new NotFoundErrorHandler(
					"Event with ID " + eventId + " does not exist"));
			Attendee a = checkInEventWithoutSaving(event, userId);
			eventAttendeeRepository.save(a);
		} else {
			throw new IllegalArgumentException(
				"Invalid code for event " + eventId);
		}
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public void manualCheckInEvent(String eventId, List<String> attendeeIds) {
		String currentUserId = userService.getCurrentUser().getId();
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!checkEventRole(event, currentUserId, OrganizerRole.CHECK_IN)) {
			throw new SecurityException(
				"Only the host, organizers with CHECKIN role, or leader can manually check-in for this event");
		}
		List<Attendee> successfulCheckIns = new ArrayList<>();
		for (String attendeeId : attendeeIds) {
			try {
				successfulCheckIns
					.add(checkInEventWithoutSaving(event, attendeeId));
			} catch (Exception e) {
				log
					.error("Check-in failed for {}: {}", attendeeId,
						e.getMessage());
				// Continue với attendee tiếp theo
			}
		}
		eventAttendeeRepository.saveAll(successfulCheckIns);
		// eventRepository.save(event);
	}

	@Override
	@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('LEADER')")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Attendee checkInEventWithoutSaving(Event event, String userId) {

		if (!checkValidTime(event)) {
			throw new IllegalStateException(
				"Không thể check-in ngoài thời gian diễn ra sự kiện");
		}
		Attendee attendee = eventAttendeeRepository
			.findByEventIdAndUserId(event.getId(), userId)
			.orElseThrow(() -> {
				throw new IllegalArgumentException("User with ID " + userId
					+ " is not registered for event " + event.getId());
			});
		attendee.checkIn();
		return attendee;
		// eventAttendeeRepository.save(attendee);
	}

	private boolean checkValidTime(Event event) {
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		return now.isAfter(event.getLocation().getStartTime())
			&& now.isBefore(event.getLocation().getEndTime());
	}

	@Override
	@Transactional
	public void triggerRegisterEvent(Event event, String userId) {
		// Kiểm tra event có phải là single không (hạn chế đăng ký trực tiếp)
		if (event.getSingle() == null || !event.getSingle()) {
			throw new IllegalStateException(
				"Cannot register for training events directly");
		}

		AttendeeId attendeeId = AttendeeId
			.builder()
			.userId(userId)
			.eventId(event.getId())
			.build();
		log.info("Trigger register event for user ID: {}", userId);
		// kiểm tra nếu đã đăng ký rồi thì bỏ đăng ký
		if (eventAttendeeRepository.existsById(attendeeId)) {
			Attendee attendee = eventAttendeeRepository
				.findById(attendeeId)
				.get();
			if (attendee.getStatus() == AttendeeStatus.BANNED) {
				throw new IllegalStateException(
					"User with ID " + userId + " is banned from this event");
			}
			if (attendee.getStatus() == AttendeeStatus.CHECKED) {
				throw new IllegalStateException("User with ID " + userId
					+ " has already checked in to this event");
			}
			if (attendee.getStatus() == AttendeeStatus.REGISTERED) {
				event.removeAttendee(attendee);
				log
					.info("User with ID {} has unregistered from event {}",
						userId, event.getId());

			} else {
				attendee.setStatus(AttendeeStatus.REGISTERED);
			}

		} else {
			// thêm mới
			event
				.addAttendee(Attendee
					.builder()
					.user(userRepository.getReferenceById(userId))
					.status(AttendeeStatus.REGISTERED)
					.build());
		}
		eventRepository.save(event);
	}

	@Override
	public void selfTriggerRegisterEvent(String eventId) {
		String currentUserId = userService.getCurrentUser().getId();
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (event instanceof Contest) {
			Contest contest = (Contest) event;
			if (!contest.isAbleToRegister()) {
				throw new IllegalStateException(
					"Không thể đăng ký cho cuộc thi không cho phép đăng ký");
			}

		}
		triggerRegisterEvent(event, currentUserId);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public void manualTriggerRegisterEvent(
		String eventId,
		List<String> attendeesIds
	) {
		String currentUserId = userService.getCurrentUser().getId();
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!checkEventRole(event, currentUserId, OrganizerRole.REGISTER)) {
			throw new SecurityException(
				"Only the host, organizers with REGISTER role, or leader can manually register for this event");
		}
		attendeesIds
			.forEach(attendeeId -> triggerRegisterEvent(event, attendeeId));
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public Event updateEventOrganizers(
		String eventId,
		List<EventOrganizerSingleRequestDto> organizerRequests
	) {
		// kiểm tra trong list chỉ được có 1 id
		Set<String> ids = new HashSet<>();
		organizerRequests.forEach(req -> {
			if (!ids.add(req.organizerId())) {
				throw new IllegalArgumentException(
					"Duplicate organizer ID in request: " + req.organizerId());
			}
		});
		User currentUser = userService.getCurrentUser();
		Event event = eventRepository
			.findByIdAndFetchOrganizers(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));

		if (!checkEventRole(event, currentUser.getId(), null)) {
			throw new SecurityException(
				"Only the host, organizers, or leader can update organizers for this event");
		}

		for (EventOrganizerSingleRequestDto req : organizerRequests) {
			if (req.removed()) {
				// Remove organizer
				EventOrganizerId eoId = EventOrganizerId
					.builder()
					.eventId(eventId)
					.organizerId(req.organizerId())
					.build();
				if (eventOrganizerRepository.existsById(eoId)) {
					event
						.removeOrganizer(
							eventOrganizerRepository.getReferenceById(eoId));
				} else {
					throw new IllegalArgumentException(
						"Organizer with ID " + req.organizerId()
							+ " is not associated with event " + eventId);
				}
			} else {
				// Add or update organizer
				User organizerUser = userRepository
					.findById(req.organizerId())
					.orElseThrow(
						() -> new IllegalArgumentException("User with ID "
							+ req.organizerId() + " does not exist"));
				if (!organizerUser.isMember()) {
					throw new IllegalArgumentException(
						"User with ID " + req.organizerId()
							+ " is not a member and cannot be an organizer");
				}
				EventOrganizer organizer = event
					.getOrganizerByUserId(req.organizerId());
				if (organizer == null) {
					// Add new organizer
					organizer = EventOrganizer
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
				} else {
					// Update existing organizer
					if (req.roleContent() != null) {
						organizer.setRoleContent(req.roleContent());
					}
					if (req.roles() != null) {
						organizer.setRoles(new HashSet<>(req.roles()));
					}
				}
			}
		}
		return eventRepository.save(event);

	}

	// hàm này để rút gọn các đoạn kiểm tra role của user hiện tại
	private boolean checkEventRole(
		Event event,
		String userId,
		OrganizerRole role
	) {
		log
			.info("current user id: {}, host id: {}", userId,
				event.getHost().getId());
		if (!event.getHost().getId().equalsIgnoreCase(userId)
			&& !ContextUtil.isLeader()) {
			// Check if the user is an organizer with the required role

			EventOrganizer organizer = eventOrganizerRepository
				.findById(EventOrganizerId
					.builder()
					.organizerId(userId)
					.eventId(event.getId())
					.build())
				.orElseThrow(() -> new SecurityException(
					"Organizer with user ID " + userId
						+ " is not associated with event " + event.getId()));
			if (!organizer.getRoles().contains(role))
				return false;

		}
		return true;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public List<EventOrganizer> getEventOrganizers(String eventId) {

		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));
		if (!event
			.getHost()
			.getUsername()
			.equalsIgnoreCase(ContextUtil.getCurrentUsername())
			&& !ContextUtil.isLeader()) {
			throw new SecurityException(
				"Only the host, organizers, or leader can view organizers for this event");
		}

		return eventOrganizerRepository.findByEventId(eventId);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	@Transactional
	public void triggerBan(
		String eventId,
		List<String> attendeesId,
		String currentUserId
	) {

		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));
		if (checkEventRole(event, currentUserId, OrganizerRole.BAN) == false) {
			throw new SecurityException(
				"Only the host, organizers with BAN role, or leader can ban attendees for this event");
		}
		if (event.isDone()) {
			throw new IllegalStateException(
				"Cannot trigger ban attendees from an event that has already been completed");
		}
		List<Attendee> attendees = new ArrayList<>();

		attendeesId.forEach(attendeeId -> {
			Attendee attendee = eventAttendeeRepository
				.findById(AttendeeId
					.builder()
					.eventId(eventId)
					.userId(attendeeId)
					.build())
				.orElseThrow(() -> new IllegalArgumentException(
					"Attendee with ID " + attendeeId
						+ " is not registered for event " + eventId));

			if (attendee.getStatus() == AttendeeStatus.BANNED) {
				attendee.setStatus(AttendeeStatus.REGISTERED);
			} else {
				attendee.setStatus(AttendeeStatus.BANNED);
			}
			attendees.add(attendee);
		});
		eventAttendeeRepository.saveAll(attendees);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public EventDetailResponseDto updateSingleEvent(
		String eventId,
		EventUpdateRequestDto dto,
		String currentUserId
	) {

		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!checkEventRole(event, currentUserId, OrganizerRole.MODIFY)) {
			throw new SecurityException(
				"Only the host, organizers with MODIFY role, or leader can modify this event");
		}
		GenerateEventFactory factory = getFactory(event);

		return factory
			.toEventDetailResponseDto(factory.updateEvent(event, dto));
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

	private GenerateEventFactory getFactory(Event event) {
		if (event instanceof Seminar) {
			return new SeminarFactory(eventMapper);
		} else if (event instanceof Contest) {
			if (((Contest) event).isAbleToRegister() == false) {
				// return new ClosedContestFactory(eventMapper);
				return new ContestFactory(eventMapper);
			}
			return new ContestFactory(eventMapper);
		} else {
			return new TrainingEventFactory(eventMapper);
		}
	}

}

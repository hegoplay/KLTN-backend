package iuh.fit.se.services.event_service.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import iuh.fit.se.entity.ExamResult;
import iuh.fit.se.entity.GlobalConfiguration;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.entity.enumerator.OrganizerRole;
import iuh.fit.se.entity.id_class.AttendeeId;
import iuh.fit.se.entity.id_class.EventOrganizerId;
import iuh.fit.se.entity.id_class.ExamResultId;
import iuh.fit.se.errorHandler.NotFoundErrorHandler;
import iuh.fit.se.repository.GlobalConfigurationRepository;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.ContestExamResultUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.TrainingEventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.mapper.EventOrganizerMapper;
import iuh.fit.se.services.event_service.patterns.factoryPattern.ContestFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.EventFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.SeminarFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.TrainingEventFactory;
import iuh.fit.se.services.event_service.repository.EventAttendeeRepository;
import iuh.fit.se.services.event_service.repository.EventOrganizerRepository;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.repository.ExamResultRepository;
import iuh.fit.se.services.event_service.service.EventCodeService;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.services.event_service.specification.EventSpecification;
import iuh.fit.se.services.training_service.repository.TrainingRepository;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.ContextUtil;
import iuh.fit.se.util.TokenContextUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
	EventCodeService eventCodeService;

	EventRepository eventRepository;
	UserRepository userRepository;
	EventOrganizerRepository eventOrganizerRepository;
	EventAttendeeRepository eventAttendeeRepository;
	TrainingRepository trainingRepository;
	ExamResultRepository examResultRepository;

	GlobalConfigurationRepository globalConfigurationRepository;

	TokenContextUtil tokenContextUtil;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	@Transactional
	public EventDetailResponseDto createEvent(SingleEventCreateRequestDto dto) {
		log.info("Creating event: {}", dto);

		if (!tokenContextUtil.getRole().isLeaderOrHigher()
			&& dto.getCategory() == EventCategory.CLOSED_CONTEST) {
			throw new IllegalArgumentException(
				"Only leaders can create closed contests");
		}

		EventFactory factory = getFactory(dto);
		Event e = factory
			.createEvent(dto,
				userRepository.getReferenceById(tokenContextUtil.getUserId()),
				userRepository);
		Event savedEvent = eventRepository.save(e);
		EventDetailResponseDto eventDetailResponseDto = factory
			.toEventDetailResponseDto(savedEvent);

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
	public Page<Event> searchUserEvents(EventSearchRequestDto request,
		String userId, FunctionStatus status) {

		Specification<Event> spec = Specification.unrestricted();

		spec = spec.and(EventSpecification.hasHostedUserId(userId));

		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public Page<Event> searchAllEvents(EventSearchRequestDto request,
		FunctionStatus status) {
		Specification<Event> spec = Specification.unrestricted();

		// Filter by status
		if (status != null) {
			spec = spec.and(EventSpecification.hasStatus(status));
		}

		return searchEvents(spec, request);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('LEADER')")
	public Page<Event> searchMyEvents(EventSearchRequestDto request,
		FunctionStatus status, String userId) {
		Specification<Event> spec = Specification.unrestricted();

		spec = spec
			.and(EventSpecification
				.hasHostedUserId(userId)
				.or(EventSpecification
					.includeOrganizerId(tokenContextUtil.getUserId())));

		if (status != null) {
			spec = spec.and(EventSpecification.hasStatus(status));
		}

		return searchEvents(spec, request);
	}

	@Override
	public Page<Event> searchRegisteredEvents(EventSearchRequestDto request,
		FunctionStatus status, String userId, AttendeeStatus attendeeStatus) {
		Specification<Event> spec = Specification.unrestricted();
		spec = spec
			.and(EventSpecification
				.includeAttendeeIdAndAttendeeStatus(userId, attendeeStatus));
		return searchEvents(spec, request);
	}

	@Override
	public EventDetailResponseDto getEventById(String eventId) {
		Event event = eventRepository
			.findByIdAndFetchAttendees(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));

		EventDetailResponseDto dto = eventMapper
			.toEventDetailResponseDto(event);

		return dto;
	}

	@Override
	@Transactional
	public EventDetailResponseDto getEventByIdAndUserId(String eventId,
		String userId) {

		Event event = eventRepository
			.findByIdAndFetchAttendees(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));

		EventDetailResponseDto dto = eventMapper
			.toEventDetailResponseDto(event);
		log.debug("EventDto fetched: {}", dto);

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

	private Page<Event> searchEvents(Specification<Event> spec,
		EventSearchRequestDto request) {

		// Keyword search (title hoặc content)
		if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
			spec = spec
				.and(EventSpecification
					.hasTitleContaining(request.getKeyword())
					.or(EventSpecification
						.hasDescriptionContaining(request.getKeyword())));
		}
		// Filter by event type
		if (request.getType() != null
			&& request.getType() != EventSearchType.ALL) {
			Class<? extends Event> eventClass = switch (request.getType()) {
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
		if (request.getIsDone() != null) {
			if (request.getIsDone()) {
				spec = spec
					.and((root, query, cb) -> cb.isTrue(root.get("isDone")));
			} else {
				spec = spec.and(EventSpecification.isNotDone());
			}
		}
		// startTime
		spec = spec
			.and(EventSpecification
				.hasTimeBetween(request.getStartTime(), request.getEndTime()));

		return eventRepository.findAll(spec, request.toPageable());
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void updateSingleEventStatus(String eventId, FunctionStatus status) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (event.getSingle() == null || !event.getSingle().booleanValue()) {
			throw new IllegalArgumentException(
				"Cannot update status of non-single events");
		}
		updateEventStatusWithoutSaving(event, status);
		eventRepository.save(event);
	}

	@Override
	public void updateEventStatusWithoutSaving(Event event,
		FunctionStatus status) {
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
		log.debug("Event {} status updated to {}", event.getId(), status);
		event.setStatus(status);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public void updateContestStanding(String eventId,
		ContestExamResultUpdateRequestDto dto) {
		// TODO: hàm này tìm kiếm và cập nhật thứ hạng của 1 contest
		String userId = tokenContextUtil.getUserId();

		Event event = eventRepository
			.findByIdAndFetchAttendees(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		// Kiểm tra các trạng thái
		if (!(event instanceof Contest contest)) {
			throw new IllegalArgumentException(
				"Event with ID " + eventId + " is not a contest");
		}
		if (contest.getStatus() != FunctionStatus.ACCEPTED) {
			throw new IllegalStateException(
				"Cannot update standings for a contest that is not ACCEPTED");
		}
		if (contest.isDone()) {
			throw new IllegalStateException(
				"Cannot update standings for a contest that is done");
		}

		Optional<EventOrganizer> eOrganizerById = eventOrganizerRepository
			.findById(EventOrganizerId
				.builder()
				.eventId(eventId)
				.organizerId(userId)
				.build());
		boolean isOrganizerWithRole = eOrganizerById
			.map(organizer -> organizer
				.getRoles()
				.contains(OrganizerRole.MODIFY))
			.orElse(false);

		if (contest.getHost().getId().equalsIgnoreCase(userId) == false
			&& tokenContextUtil.getRole().isLeaderOrHigher() == false
			&& isOrganizerWithRole == false) {
			throw new SecurityException(
				"Only the host or leader or organizer with MODIFY roles can update standings for this contest");
		}
		// Update thứ hạng
		List<ExamResult> examResult = examResultRepository
			.findAllByContest(contest);
		examResultRepository.deleteAll(examResult);
		List<ExamResult> newResults = new ArrayList<>();
		for (var examResultDto : dto.getExamResults()) {
			Attendee attendee = event
				.getAttendeesMap()
				.get(examResultDto.studentId());
			if (attendee == null) {
				throw new IllegalArgumentException(
					"Student with ID " + examResultDto.studentId()
						+ " is not an attendee of contest " + eventId);
			}
			var examResultEntity = ExamResult
				.builder()
				.contest(contest)
				.student(attendee.getUser())
				.point(examResultDto.point())
				.rank(examResultDto.rank())
				.examResultId(ExamResultId
					.builder()
					.contestId(contest.getId())
					.studentId(examResultDto.studentId())
					.build())
				.build();
			newResults.add(examResultEntity);
		}
		examResultRepository.saveAll(newResults);

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
				updateContestUsersScore(contest, event.getMultiple());
			} else {
				updateEventUsersScore(event, event.getMultiple());
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
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		// Check if the current user is the host or an attendee
		String userId = tokenContextUtil.getUserId();
		// Check if the user is the host, an organizer, or a leader
		if (!checkEventRole(event, userId, OrganizerRole.CODE)) {
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
		String currentUserId = tokenContextUtil.getUserId();
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
			successfulCheckIns
				.add(checkInEventWithoutSaving(event, attendeeId));
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
		Event e;
		// kiểm tra nếu đã đăng ký rồi thì bỏ đăng ký
		if (eventAttendeeRepository.existsById(attendeeId)) {
			e = unregisterEventWithoutSaving(event, userId);

		} else {
			e = registerEventWithoutSaving(event, userId);
		}
		if (e.getAttendeesMap().size() > event.getLimitRegister()) {
			throw new IllegalStateException("Event with ID " + event.getId()
				+ " has reached its registration limit");
		}
		eventRepository.save(e);
	}

	@Override
	// hàm này sẽ không check điều kiện gì cả, chỉ thêm thẳng user vào event
	public Event registerEventWithoutSaving(Event event, String userId) {
		event
			.addAttendee(Attendee
				.builder()
				.user(userRepository.getReferenceById(userId))
				.status(AttendeeStatus.REGISTERED)
				.build());
		return event;
	}

	@Override
	public Event unregisterEventWithoutSaving(Event event, String userId) {
		AttendeeId attendeeId = AttendeeId
			.builder()
			.userId(userId)
			.eventId(event.getId())
			.build();

		Attendee attendee = eventAttendeeRepository.findById(attendeeId).get();
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
				.info("User with ID {} has unregistered from event {}", userId,
					event.getId());

		} else {
			attendee.setStatus(AttendeeStatus.REGISTERED);
		}
		return event;
	}

	@Override
	public void selfTriggerRegisterEvent(String eventId) {
		String currentUserId = tokenContextUtil.getUserId();
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
	public void manualTriggerRegisterEvent(String eventId,
		List<String> attendeesIds) {
		String currentUserId = tokenContextUtil.getUserId();
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
	public Event patchEventOrganizers(String eventId,
		List<EventOrganizerSingleRequestDto> organizerRequests) {
		// kiểm tra trong list chỉ được có 1 id
		Set<String> ids = new HashSet<>();
		organizerRequests.forEach(req -> {
			if (!ids.add(req.organizerId())) {
				throw new IllegalArgumentException(
					"Duplicate organizer ID in request: " + req.organizerId());
			}
		});
		Event event = eventRepository
			.findByIdAndFetchOrganizers(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));

		EventFactory factory = EventFactory
			.getFactory(event, eventMapper, trainingRepository);

		factory.checkType();

		if (!checkEventRole(event, tokenContextUtil.getUserId(), null)) {
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
				event = factory
					.addOrUpdateOrganizerToEvent(event, req, userRepository);
			}
		}
		return eventRepository.save(event);
	}

	@Override
	public Event updateEventOrganizers(String eventId,
		List<EventOrganizerSingleRequestDto> organizerRequests) {

		Set<String> ids = new HashSet<>();
		organizerRequests.forEach(req -> {
			if (!ids.add(req.organizerId())) {
				throw new IllegalArgumentException(
					"Duplicate organizer ID in request: " + req.organizerId());
			}
		});
		Event event = eventRepository
			.findByIdAndFetchOrganizers(eventId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Event with ID " + eventId + " does not exist"));

		EventFactory factory = EventFactory
			.getFactory(event, eventMapper, trainingRepository);

		if (!checkEventRole(event, tokenContextUtil.getUserId(), null)) {
			throw new SecurityException(
				"Only the host, organizers, or leader can update organizers for this event");
		}
		event.clearOrganizers();
		for (EventOrganizerSingleRequestDto req : organizerRequests) {
			event = factory
				.addOrUpdateOrganizerToEvent(event, req, userRepository);
		}

		return eventRepository.save(event);
	}

	// hàm này để rút gọn các đoạn kiểm tra role của user hiện tại
	private boolean checkEventRole(Event event, String userId,
		OrganizerRole role) {
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

		return eventOrganizerRepository.findAllByEvent(event);
	}

	@Override
	public List<String> getReviewsForSeminarEvent(String eventId,
		String getterId) {

		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!(event instanceof Seminar seminar)) {
			throw new IllegalArgumentException(
				"Event with ID " + eventId + " is not a seminar");
		}
		// Check if the current user is the host or leader
		if (!seminar.getHost().getId().equalsIgnoreCase(getterId)
			&& !ContextUtil.isLeader()) {
			throw new SecurityException(
				"Only the host or leader can view reviews for this seminar");
		}
		return seminar.getReviews();

	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	@Transactional
	public void triggerBan(String eventId, List<String> attendeesId,
		String currentUserId) {

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
	public EventDetailResponseDto updateEvent(String eventId,
		EventUpdateRequestDto dto, String currentUserId) {

		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!checkEventRole(event, currentUserId, OrganizerRole.MODIFY)) {
			throw new SecurityException(
				"Only the host, organizers with MODIFY role, or leader can modify this event");
		}
		EventFactory factory = EventFactory
			.getFactory(event, eventMapper, trainingRepository);

		Event updatedEvent = factory.updateEvent(event, dto);
		eventRepository.save(updatedEvent);

		return factory.toEventDetailResponseDto(updatedEvent);
	}

	private EventFactory getFactory(BaseEventCreateRequestDto dto) {

		if (dto instanceof TrainingEventCreateRequestDto) {
			return new TrainingEventFactory(eventMapper, trainingRepository);
		}
		if (dto instanceof SingleEventCreateRequestDto singleDto) {
			return switch (singleDto.getCategory()) {
				case SEMINAR -> new SeminarFactory(eventMapper);
				case CONTEST -> new ContestFactory(eventMapper);
				case CLOSED_CONTEST -> new ContestFactory(eventMapper);
				// ClosedContestFactory(eventMapper);
				default -> throw new IllegalArgumentException(
					"Unsupported event type: " + singleDto.getCategory());
			};
		}
		throw new IllegalArgumentException("Unsupported event type: ");

	}

	@Override
	@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('LEADER')")
	public void addReviewForSeminarEvent(String eventId, String userId,
		String reviewContent) {
		Event event = eventRepository
			.findById(eventId)
			.orElseThrow(() -> new NotFoundErrorHandler(
				"Event with ID " + eventId + " does not exist"));
		if (!(event instanceof Seminar seminar)) {
			throw new IllegalArgumentException(
				"Event with ID " + eventId + " is not a seminar");
		}

		Attendee attendee = event.getAttendeeByUserId(userId);
		if (attendee == null
			|| attendee.getStatus() != AttendeeStatus.CHECKED) {
			throw new IllegalStateException(
				"Chỉ có những ngươi tham gia sự kiện và đã điểm danh mới được đánh giá");
		}
		seminar.addReview(reviewContent);
		eventRepository.save(seminar);
	}

	// private EventFactory getFactory(Event event) {
	// if (event instanceof Seminar) {
	// return new SeminarFactory(eventMapper);
	// } else if (event instanceof Contest) {
	// if (((Contest) event).isAbleToRegister() == false) {
	// // return new ClosedContestFactory(eventMapper);
	// return new ContestFactory(eventMapper);
	// }
	// return new ContestFactory(eventMapper);
	// } else {
	// return new TrainingEventFactory(eventMapper, trainingRepository);
	// }
	// }

}

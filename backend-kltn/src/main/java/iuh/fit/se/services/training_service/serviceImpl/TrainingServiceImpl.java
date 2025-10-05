package iuh.fit.se.services.training_service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iuh.fit.se.common.enumerator.RequestFunctionStatus;
import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.errorHandler.NotFoundErrorHandler;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.TrainingEventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.patterns.factoryPattern.EventFactory;
import iuh.fit.se.services.event_service.patterns.factoryPattern.TrainingEventFactory;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.services.training_service.dto.TrainingCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingEventListCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingPatchRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingSearchDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.training_service.mapper.TrainingMapper;
import iuh.fit.se.services.training_service.repository.TrainingEventRepository;
import iuh.fit.se.services.training_service.repository.TrainingRepository;
import iuh.fit.se.services.training_service.service.TrainingService;
import iuh.fit.se.services.training_service.specification.EntitySpecification;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.util.TimeCheckUtil;
import iuh.fit.se.util.TokenContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

	private final TrainingMapper trainingMapper;
	private final EventMapper eventMapper;

	private final TrainingRepository trainingRepository;
	private final UserRepository userRepository;
	private final TrainingEventRepository trainingEventRepository;
	private final EventRepository eventRepository;

	private final EventService eventService;

	private final TokenContextUtil tokenContextUtil;

	@Override
	public TrainingDetailDto createTraining(TrainingCreateRequestDto dto) {
		if (!dto.getStatus().isAbleToCreate()) {
			throw new IllegalArgumentException(
				"Chỉ được phép khởi tạo khóa học với trạng thái ARCHIVED hoặc PENDING");
		}

		TimeCheckUtil
			.checkCreateObjectValid(dto.getLocation().getStartTime(),
				dto.getLocation().getEndTime());

		dto.getTrainingEvents().forEach(eventDto -> {
			EventFactory.checkEventCreation(eventDto);
		});

		log.info("Creating training: {}", dto);

		Training training = trainingMapper.toTraining(dto);

		training.setAllEventStatus(dto.getStatus());

		training
			.setCreator(
				userRepository.getReferenceById(tokenContextUtil.getUserId()));
		dto.getMentorIds().forEach(id -> {
			training.addMentor(userRepository.getReferenceById(id));
		});

		training
			.setAllEventHost(
				userRepository.getReferenceById(tokenContextUtil.getUserId()));

		EventFactory factory = new TrainingEventFactory(eventMapper,
			trainingRepository);

		training.setTrainingEvents(new ArrayList<>());

		for (var eventDto : dto.getTrainingEvents()) {

			Event event = factory
				.createEvent(
					TrainingEventCreateRequestDto
						.fromBaseEventDto(eventDto, null),
					userRepository
						.getReferenceById(tokenContextUtil.getUserId()));
			if (event instanceof TrainingEvent trainingEvent) {
				training.addTrainingEvent(trainingEvent);
			} else {
				throw new IllegalArgumentException(
					"Sự kiện không phải là sự kiện đào tạo");
			}
		}

		Training newTraining = trainingRepository.save(training);
		return trainingMapper.toTrainingDetailDto(newTraining);
	}

	@Override
	public Page<TrainingWrapperDto> getMyTrainings(
		TrainingSearchDto dto,
		FunctionStatus status
	) {
		Specification<Training> spec = Specification.unrestricted();

		spec = spec
			.and(EntitySpecification
				.<Training>hasCreatorId(tokenContextUtil.getUserId())
				// TODO: sửa lại hàm hasMentorId
				.or(EntitySpecification
					.hasMentorId(tokenContextUtil.getUserId())));
		
		if (status != null)
			spec = spec.and(EntitySpecification.hasStatus(status));
		return searchTrainings(spec, dto);
	}

	@Override
	public Page<TrainingWrapperDto> getPublicTrainings(TrainingSearchDto dto) {
		Specification<Training> spec = Specification.unrestricted();

		spec = spec.and(EntitySpecification.hasStatus(FunctionStatus.ACCEPTED));
		return searchTrainings(spec, dto);
	}

	@Override
	public Page<TrainingWrapperDto> getAllTrainings(
		TrainingSearchDto dto,
		FunctionStatus status
	) {
		Specification<Training> spec = Specification.unrestricted();

		if (status != null)
			spec = spec.and(EntitySpecification.hasStatus(status));
		return searchTrainings(spec, dto);
	}

	/*
	 * hàm này cho phép xóa khóa học nếu người dùng có role là ADMIN, LEADER
	 * hoặc MEMBER
	 */
	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	@Transactional
	public void deleteTraining(String trainingId) {
		Training training = trainingRepository
			.findById(trainingId)
			.orElseThrow(
				() -> new IllegalArgumentException("Khóa học không tồn tại"));
		if (!tokenContextUtil.getRole().isLeaderOrHigher() && !training
			.getCreator()
			.getId()
			.equals(tokenContextUtil.getUserId())) {
			throw new SecurityException(
				"Chỉ có người tạo khóa học mới được phép xóa");
		}

		List<TrainingEvent> trainingEvents = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));

		trainingEvents.forEach(event -> {
			eventService.deleteEvent(event.getId());
		});

		trainingRepository.delete(training);

	}

	@Override
	public Page<TrainingWrapperDto> getRegisteredTrainings(
		TrainingSearchDto dto) {

		Specification<Training> spec = Specification.unrestricted();

		String userId = tokenContextUtil.getUserId();
		spec = spec.and(EntitySpecification.hasParticipantId(userId));
		
		return searchTrainings(spec, dto);
	}

	@Override
	public Page<TrainingWrapperDto> searchTrainings(
		Specification<Training> spec,
		TrainingSearchDto dto
	) {
		if (dto.getKeyword() != null) {
			spec = spec
				.and(EntitySpecification
					.<Training>hasDescription(dto.getKeyword())
					.or(EntitySpecification.hasTitle(dto.getKeyword())));
		}
		spec = spec
			.and(EntitySpecification
				.hasTimeBetween(dto.getStartTime(), dto.getEndTime()));
		return trainingRepository
			.findAll(spec, dto.toPageable())
			.map(trainingMapper::toTrainingWrapperDto);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	@Transactional
	public void changeTrainingStatus(String trainingId, FunctionStatus status) {
		Training training = trainingRepository
			.findById(trainingId)
			.orElseThrow(
				() -> new IllegalArgumentException("Khóa học không tồn tại"));
		List<TrainingEvent> allByTraining_Id = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));

		allByTraining_Id.forEach(event -> {
			event.setStatus(status);
		});
		training.setStatus(status);
		trainingEventRepository.saveAll(allByTraining_Id);
		trainingRepository.save(training);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	public TrainingDetailDto getTrainingById(String trainingId) {

		List<TrainingEvent> events = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));

		Training currentTraining = trainingRepository
			.findByIdFetchParticipants(trainingId)
			.orElseThrow(
				() -> new IllegalArgumentException("Khóa học không tồn tại"));
		currentTraining.setTrainingEvents(events);

		return trainingMapper.toTrainingDetailDto(currentTraining);
	}

	@Override
	public TrainingDetailDto getPublicTrainingById(String trainingId) {
		TrainingDetailDto trainingById = getTrainingById(trainingId);

		if (trainingById.getStatus() != FunctionStatus.ACCEPTED) {
			throw new SecurityException(
				"Chỉ có thể xem chi tiết khóa học công khai khi khóa học đã được duyệt");
		}
		List<EventWrapperDto> trainingEvents = trainingById.getTrainingEvents();
		trainingEvents
			.removeIf(event -> event.getStatus() != FunctionStatus.ACCEPTED);
		trainingEvents
			.sort((e1, e2) -> e1
				.getLocation()
				.getStartTime()
				.compareTo(e2.getLocation().getStartTime()));
		trainingById.setTrainingEvents(trainingEvents);
		log.debug("trainingEvents sau khi filter: {}", trainingEvents);
		return trainingById;
	}

	@Override
	public TrainingDetailDto getMyTrainingById(String trainingId) {
		TrainingDetailDto trainingById = getTrainingById(trainingId);

		if (!trainingById
			.getCreator()
			.getId()
			.equals(tokenContextUtil.getUserId())
			&& trainingById
				.getMentors()
				.stream()
				.noneMatch(mentor -> mentor
					.getId()
					.equals(tokenContextUtil.getUserId()))) {
			throw new SecurityException(
				"Chỉ có thể xem chi tiết khóa học của mình");
		}
		return trainingById;
	}

	@Override
	@Transactional
	public void registerTraining(String trainingId, String userId) {
		var training = trainingRepository
			.findById(trainingId)
			.orElseThrow(
				() -> new IllegalArgumentException("Khóa học không tồn tại"));
		List<TrainingEvent> events = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));
		List<Event> updatedEvents = new ArrayList<>();
		events.forEach(event -> {
			updatedEvents
				.add(eventService.registerEventWithoutSaving(event, userId));
		});
		eventRepository.saveAll(events);
		training.addParticipant(userRepository.getReferenceById(userId));
		trainingRepository.save(training);
	}

	@Override
	@Transactional
	public void selfRegisterTraining(String trainingId) {
		String userId = tokenContextUtil.getUserId();
		registerTraining(trainingId, userId);

	}

	@Override
	@Transactional
	public void unregisterTraining(String trainingId, String userId) {
		Training fetchedTraining = trainingRepository
			.findByIdFetchParticipants(trainingId)
			.orElseThrow(
				() -> new IllegalArgumentException("Khóa học không tồn tại"));
		List<TrainingEvent> events = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));
		List<Event> updatedEvents = new ArrayList<>();
		events.forEach(event -> {
			updatedEvents
				.add(eventService.unregisterEventWithoutSaving(event, userId));
		});
		eventRepository.saveAll(events);
		fetchedTraining
			.removeParticipant(userRepository.getReferenceById(userId));
	}

	@Override
	public void selfUnregisterTraining(String trainingId) {
		String userId = tokenContextUtil.getUserId();
		unregisterTraining(trainingId, userId);
	}

	@Override
	@Transactional
	public void selfTriggerRegisterTraining(String trainingId) {
		String userId = tokenContextUtil.getUserId();
		// lấy training
		Training training = trainingRepository
			.findByIdFetchParticipants(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));
		// lấy tất cả các event của training
		List<TrainingEvent> events = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));
		// kiểm tra user đã đăng ký hay chưa
		boolean isUserRegistered = training
			.getParticipants()
			.stream()
			.anyMatch(participant -> participant.getId().equals(userId));

		List<Event> updatedEvents = new ArrayList<>();
		// nếu chưa đăng ký thì đăng ký, nếu đã đăng ký thì hủy đăng ký
		if (!isUserRegistered) {
			events.forEach(event -> {
				updatedEvents
					.add(
						eventService.registerEventWithoutSaving(event, userId));
			});
		} else {
			events.forEach(event -> {
				updatedEvents
					.add(eventService
						.unregisterEventWithoutSaving(event, userId));
			});
			training.removeParticipant(userRepository.getReferenceById(userId));
		}
		eventRepository.saveAll(events);
		training.addParticipant(userRepository.getReferenceById(userId));
		trainingRepository.save(training);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void manualTriggerRegisterTraining(
		String trainingId,
		List<String> userIds
	) {

		Training training = trainingRepository
			.findByIdFetchParticipants(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));
		// check role
		if (!allowToModify(training) && !training
			.getMentors()
			.stream()
			.noneMatch(mentor -> mentor
				.getId()
				.equals(tokenContextUtil.getUserId()))) {
			throw new SecurityException(
				"Chỉ có người tạo khóa học hoặc leader mới được phép thực hiện hành động này");
		}

		// lấy danh sách userId đã đăng ký và chưa đăng ký
		List<String> registedUserIds = userIds
			.stream()
			.filter(userId -> training
				.getParticipants()
				.stream()
				.anyMatch(participant -> participant.getId().equals(userId)))
			.toList();
		List<String> unregistedUserIds = userIds
			.stream()
			.filter(userId -> !registedUserIds.contains(userId))
			.toList();

		// cập nhật lại danh sách người tham gia của khóa học
		List<TrainingEvent> events = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));
		List<Event> updatedEvents = new ArrayList<>();

		for (var event : events) {
			var tempEvent = (Event) event;
			for (var userId : unregistedUserIds) {
				tempEvent = eventService
					.registerEventWithoutSaving(tempEvent, userId);
			}
			for (var userId : registedUserIds) {
				tempEvent = eventService
					.unregisterEventWithoutSaving(tempEvent, userId);
			}
			updatedEvents.add(tempEvent);
		}
		eventRepository.saveAll(updatedEvents);

	}

	@Override
	public boolean checkIfUserRegistered(String trainingId, String userId) {
		return trainingRepository
			.participantsExistByIdAndTraining_Id(userId, trainingId);
	}

	@Override
	@Transactional
	public TrainingDetailDto insertTrainingEvents(
		String trainingId,
		TrainingEventListCreateRequestDto dto
	) {
		// thực hiện việc thêm sự kiện vào trong khóa học và thêm thông
		// tin của những người đã đăng ký vào trong khóa học,
		// Lấy thông tin khóa học
		Training training = trainingRepository
			.findByIdFetchParticipants(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));
		// lấy factory
		EventFactory factory = new TrainingEventFactory(eventMapper,
			trainingRepository);
		// tạo map người tham gia
		Map<String, Attendee> attendees = new java.util.HashMap<>();
		training.getParticipants().forEach(participant -> {
			Attendee attendee = Attendee
				.builder()
				.user(participant)
				.userId(participant.getId())
				.status(AttendeeStatus.REGISTERED)
				.build();
			attendees.put(participant.getId(), attendee);
		});

		for (var eventDto : dto.getEvents()) {
			if (eventDto.getCategory() == null) {
				eventDto.setCategory(EventCategory.TRAINING_EVENT);
			}
			if (eventDto.getCategory() != EventCategory.TRAINING_EVENT) {
				throw new IllegalArgumentException(
					"Sự kiện không phải là sự kiện đào tạo");
			}
			// kiểm tra tính hợp lệ của sự kiện
			EventFactory.checkEventCreation(eventDto);
			// tạo sự kiện
			TrainingEventCreateRequestDto tEventDto = TrainingEventCreateRequestDto
				.fromBaseEventDto(eventDto, trainingId);
			Event event = factory
				.createEvent(tEventDto, userRepository
					.getReferenceById(tokenContextUtil.getUserId()));
			if (event instanceof TrainingEvent trainingEvent) {
				// thêm danh sách những người đã đăng ký vào trong sự kiện
				trainingEvent.setAttendeesMap(attendees);
				training.addTrainingEvent(trainingEvent);

			} else {
				throw new IllegalArgumentException(
					"Sự kiện không phải là sự kiện đào tạo");
			}
		}
		training = trainingRepository.save(training);
		return trainingMapper.toTrainingDetailDto(training);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER') or hasRole('MEMBER')")
	@Transactional
	public TrainingDetailDto updateWrapperInformation(
		String trainingId,
		TrainingPatchRequestDto dto
	) {
		// Viết hàm để update những thông tin cơ bản của khóa học
		Training training = trainingRepository
			.findById(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));

		trainingMapper.updateTrainingFromDtoNullIgnore(dto, training);

		var trainingEventsList = trainingEventRepository
			.findAllByTraining(trainingRepository.getReferenceById(trainingId));

		trainingEventsList.forEach(event -> {
			event
				.setStatus(RequestFunctionStatus
					.convertToFunctionStatus(dto.status()));
		});

		trainingEventRepository.saveAll(trainingEventsList);

		Training updatedTraining = trainingRepository.save(training);
		return trainingMapper.toTrainingDetailDto(updatedTraining);

	}

	@Override
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	@Transactional
	public void updateTrainingStatus(String trainingId, FunctionStatus status) {
		// viết hàm để update trạng thái của khóa học và các sự kiện con
		// bên trong nó
		Training training = trainingRepository
			.findById(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));
		// lấy tất cả các sự kiện bên trong khóa học
		List<TrainingEvent> events = trainingEventRepository.findAll();
		log.debug("events in updateTrainingStatus: {}", events);
		for (var event : events) {
			eventService.updateEventStatusWithoutSaving(event, status);
		}
		trainingEventRepository.saveAll(events);

		training.setStatus(status);
		trainingRepository.save(training);
	}

	@Override
	@Transactional
	public void updateTrainingMentors(
		String trainingId,
		List<String> addingMentorIds,
		List<String> removingMentorIds
	) {
		Training training = trainingRepository
			.findByIdFetchMentors(trainingId)
			.orElseThrow(
				() -> new NotFoundErrorHandler("Khóa học không tồn tại"));
		if (!tokenContextUtil.getRole().isLeaderOrHigher()
			&& tokenContextUtil.getUserId() != training.getCreator().getId()) {
			throw new SecurityException(
				"Chỉ có người tạo khóa học hoặc leader mới được phép thêm hoặc xóa mentor");
		}
		if (!Objects.isNull(removingMentorIds))
			removingMentorIds.forEach(id -> {
				training.removeMentor(userRepository.getReferenceById(id));
			});
		if (!Objects.isNull(addingMentorIds))
			addingMentorIds.forEach(id -> {
				training.addMentor(userRepository.getReferenceById(id));
			});

		trainingRepository.save(training);
	}

	private boolean allowToModify(Training training) {
		return training
			.getCreator()
			.getId()
			.equals(tokenContextUtil.getUserId())
			|| tokenContextUtil.getRole().isLeaderOrHigher();
	}
}

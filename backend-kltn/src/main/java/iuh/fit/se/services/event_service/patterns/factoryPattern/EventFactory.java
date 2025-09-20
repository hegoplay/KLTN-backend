package iuh.fit.se.services.event_service.patterns.factoryPattern;

import java.time.LocalDateTime;
import java.util.HashSet;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.training_service.repository.TrainingRepository;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.util.TimeCheckUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EventFactory {
	// public

	public Event createEvent(
		BaseEventCreateRequestDto dto,
		User host
	) {

		checkEventCreation(dto);

		// Generate the event using the abstract method
		Event e = generateEvent(dto);
		if (dto.getMultiple() == null) {
			e.setMultiple(1);
		}
		
		e.setHost(host);

		return e;
	}
	public abstract EventDetailResponseDto toEventDetailResponseDto(Event e);

	public Event updateEvent(Event event, EventUpdateRequestDto dto) {
		if (event.isDone()) {
			throw new IllegalStateException(
				"Cannot modify an event that has already been completed");
		}
		if (dto.status() == null) {
			throw new IllegalArgumentException("Event status cannot be null");
		}
//		if (event.getSingle() != null && event.getSingle() == Boolean.FALSE) {
//			// nếu là training event thì không được phép chỉnh sửa status
//			if (dto.status() != event.getStatus()) {
//				throw new IllegalArgumentException(
//					"Cannot change status of training events");
//			}
//		}
		if (!(dto.status() == FunctionStatus.PENDING
			|| dto.status() == FunctionStatus.ARCHIVED)) {
			throw new IllegalArgumentException(
				"Event status must be either PENDING or ARCHIVED");
		}
		if (dto.location() != null) {
			if (dto.location().getStartTime() != null && dto
				.location()
				.getStartTime()
				.isBefore(LocalDateTime.now())) {
				throw new IllegalArgumentException(
					"Thời gian bắt đầu phải sau ngày hôm nay");
			}
			if(dto.location().getEndTime() !=null && dto.location().getStartTime() != null) {
				TimeCheckUtil.checkCreateObjectValid(dto.location().getStartTime(),
					dto.location().getEndTime());
			}
			else if (dto.location().getEndTime() != null) {
				TimeCheckUtil.checkCreateObjectValid(event.getLocation().getStartTime(),
					dto.location().getEndTime());
			}
//			kiểm tra endTime > startTime
			
		}
		return handleUpdateEvent(event, dto);
	}

	public Event addOrUpdateOrganizerToEvent(
		Event event,
		EventOrganizerSingleRequestDto req,
		UserRepository userRepository
	) {
		User organizerUser = userRepository
			.findById(req.organizerId())
			.orElseThrow(() -> new IllegalArgumentException(
				"User with ID " + req.organizerId() + " does not exist"));
		if (!organizerUser.isMember()) {
			throw new IllegalArgumentException(
				"User with ID " + req.organizerId()
					+ " is not a member and cannot be an organizer");
		}
		EventOrganizer organizer = event
			.getOrganizerByUserId(req.organizerId());
		if (organizer == null) {
			addNewOrganizerToEvent(event, req, organizerUser);
		} else {
			// Update existing organizer
			if (req.roleContent() != null) {
				organizer.setRoleContent(req.roleContent());
			}
			if (req.roles() != null) {
				organizer.setRoles(new HashSet<>(req.roles()));
			}
		}

		return event;
	}

	protected abstract void addNewOrganizerToEvent(
		Event event,
		EventOrganizerSingleRequestDto req,
		User organizerUser
	);

	protected abstract Event generateEvent(BaseEventCreateRequestDto dto);

	protected abstract Event handleUpdateEvent(
		Event e,
		EventUpdateRequestDto dto
	);

	public static void checkEventCreation(BaseEventCreateRequestDto dto) {
		if (dto instanceof SingleEventCreateRequestDto) {
			SingleEventCreateRequestDto singleDto = (SingleEventCreateRequestDto) dto;
			if (!singleDto.isCreateAble()) {
				throw new IllegalArgumentException(
					"Event cannot be created with the provided status: "
						+ singleDto.getStatus());
			}
		}

		TimeCheckUtil
			.checkCreateObjectValid(dto.getLocation().getStartTime(),
				dto.getLocation().getEndTime());

	}

	// public
	public void checkType(Event e) {
		if (e instanceof Contest) {
			log.info("Event is a Contest");
		}
	}

	public static EventFactory getFactory(
		Event event,
		EventMapper eventMapper,
		TrainingRepository trainingRepository
	) {
		if (event instanceof Seminar) {
			return new SeminarFactory(eventMapper);
		} else if (event instanceof Contest) {
			if (((Contest) event).isAbleToRegister() == false) {
				// return new ClosedContestFactory(eventMapper);
				return new ContestFactory(eventMapper);
			}
			return new ContestFactory(eventMapper);
		} else {
			return new TrainingEventFactory(eventMapper, trainingRepository);
		}
	}
}

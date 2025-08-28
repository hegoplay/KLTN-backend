package iuh.fit.se.services.event_service.patterns.factoryPattern;

import java.time.LocalDateTime;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.user_service.service.UserService;

public abstract class GenerateEventFactory {
	// public

	public Event createEvent(
		EventCreateRequestDto dto,
		UserService userService
	) {

		if (!dto.isCreateAble()) {
			throw new IllegalArgumentException(
				"Event cannot be created with the provided status: "
					+ dto.status());
		}
		if (dto
			.location()
			.getEndTime()
			.isBefore(dto.location().getStartTime())) {
			throw new IllegalArgumentException(
				"End time must be after start time");
		}
		if (dto.location().getStartTime().isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException(
				"Start time must be in the future");
		}

		// Generate the event using the abstract method
		Event e = generateEvent(dto);
		if (dto.status() == null) {
			e.setStatus(FunctionStatus.ARCHIVED);
		}
		if (dto.multiple() == null) {
			e.setMultiple(1);
		}

		User currentUser = userService.getCurrentUser();
		e.setHost(currentUser);

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
		if (event.getSingle() != null && event.getSingle() == Boolean.FALSE) {
			// nếu là training event thì không được phép chỉnh sửa status
			if (dto.status() != event.getStatus()) {
				throw new IllegalArgumentException(
					"Cannot change status of training events");
			}
		}
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
		

		}
		return handleUpdateEvent(event, dto);
	}

	protected abstract Event generateEvent(EventCreateRequestDto dto);

	protected abstract Event handleUpdateEvent(
		Event e,
		EventUpdateRequestDto dto
	);

}

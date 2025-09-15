package iuh.fit.se.services.event_service.mapper;

import java.time.LocalDateTime;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.enumerator.EventTimeStatus;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.TrainingEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(
	componentModel = "spring",
	uses = {EventOrganizerMapper.class, UserMapper.class})
public abstract class EventMapper {

	public abstract Seminar toSeminar(SingleEventCreateRequestDto dto);
	public abstract Contest toContest(SingleEventCreateRequestDto dto);
	// Add other event types as needed
	// bổ sung phần after mapping để set training sau
	public abstract TrainingEvent toTrainingEvent(
		TrainingEventCreateRequestDto dto
	);

	public abstract EventDetailResponseDto toEventDetailResponseDto(
		Seminar event
	);
	public abstract EventDetailResponseDto toEventDetailResponseDto(
		Contest event
	);
	public abstract EventDetailResponseDto toEventDetailResponseDto(
		TrainingEvent event
	);
	public EventDetailResponseDto toEventDetailResponseDto(Event event) {

		EventDetailResponseDto dto;

		if (event instanceof Seminar seminar) {
			dto = toEventDetailResponseDto(seminar);
		} else if (event instanceof Contest contest) {
			dto = toEventDetailResponseDto(contest);
		} else if (event instanceof TrainingEvent trainingEvent) {
			dto = toEventDetailResponseDto(trainingEvent);
			dto.setTrainingId(trainingEvent.getTraining().getId());
		} else {
			throw new IllegalArgumentException(
				"Unknown event type: " + event.getClass());
		}

		return dto;
	}

	public abstract EventWrapperDto toEventWrapperDto(Event event);

	@AfterMapping
	public void afterEventWrapperDto(
		@MappingTarget EventWrapperDto.EventWrapperDtoBuilder<?, ?> builder,
		Event event
	) {
		builder.done(Boolean.valueOf(event.isDone()));
		if (event.isDone()) {
			builder.timeStatus(EventTimeStatus.LOCKED);
		} else if (event
			.getLocation()
			.getStartTime()
			.isAfter(LocalDateTime.now())) {
			builder.timeStatus(EventTimeStatus.UPCOMING);
		} else if (event
			.getLocation()
			.getEndTime()
			.isBefore(LocalDateTime.now())) {
			builder.timeStatus(EventTimeStatus.COMPLETED);
		} else {
			builder.timeStatus(EventTimeStatus.ONGOING);
		}

		if (event instanceof Seminar) {
			builder.category(EventCategory.SEMINAR);
		} else if (event instanceof Contest contest) {
			if (contest.isAbleToRegister()) {
				builder.category(EventCategory.CONTEST);
			} else {
				builder.category(EventCategory.CLOSED_CONTEST);
			}
		} else if (event instanceof TrainingEvent) {
			builder.category(EventCategory.TRAINING_EVENT);
		} else {
			throw new IllegalArgumentException(
				"Unknown event type: " + event.getClass());
		}
	}

	@BeanMapping(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract void updateEventFromDto(
		EventUpdateRequestDto dto,
		@MappingTarget Contest event
	);

	@BeanMapping(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract void updateEventFromDto(
		EventUpdateRequestDto dto,
		@MappingTarget TrainingEvent event
	);

	@BeanMapping(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract void updateEventFromDto(
		EventUpdateRequestDto dto,
		@MappingTarget Event event
	);

	@BeanMapping(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract void updateEventFromDto(
		EventUpdateRequestDto dto,
		@MappingTarget Seminar event
	);
}

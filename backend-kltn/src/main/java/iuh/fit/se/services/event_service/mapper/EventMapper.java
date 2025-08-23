package iuh.fit.se.services.event_service.mapper;

import java.time.LocalDateTime;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventTimeStatus;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(
	componentModel = "spring",
	uses = {EventOrganizerMapper.class, UserMapper.class}
)
public abstract class EventMapper {

	public abstract Seminar toSeminar(EventCreateRequestDto dto);
	public abstract Contest toContest(EventCreateRequestDto dto);
	// Add other event types as needed
	// bổ sung phần after mapping để set training sau
	public abstract TrainingEvent toTrainingEvent(EventCreateRequestDto dto);

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
		if (event instanceof Seminar seminar) {
			return toEventDetailResponseDto(seminar);
		} else if (event instanceof Contest contest) {
			return toEventDetailResponseDto(contest);
		} else if (event instanceof TrainingEvent trainingEvent) {
			return toEventDetailResponseDto(trainingEvent);
		} else {
			throw new IllegalArgumentException(
				"Unknown event type: " + event.getClass());
		}
	}

	@AfterMapping
	public void afterEventDetailResponseDto(
		@MappingTarget EventDetailResponseDto.EventDetailResponseDtoBuilder builder,
		TrainingEvent event
	) {
		builder.isDone(Boolean.valueOf(event.isDone()));
		if (event.getTraining() != null) {
			builder.trainingId(event.getTraining().getId());
		}
	}
	public abstract EventWrapperDto toEventWrapperDto(Event event);

	@AfterMapping
	public void afterEventWrapperDto(
		@MappingTarget EventWrapperDto.EventWrapperDtoBuilder builder,
		Event event
	) {
		builder.isDone(Boolean.valueOf(event.isDone()));
		if (event.isDone()) {
			builder.timeStatus(EventTimeStatus.LOCKED);
		} else if (event
			.getLocation()
			.getStartTime()
			.isAfter(LocalDateTime.now())) {
			builder.timeStatus(EventTimeStatus.UPCOMING);
		} else if (event
			.getLocation()
			.getStartTime()
			.isBefore(LocalDateTime.now())) {
			builder.timeStatus(EventTimeStatus.COMPLETED);
		} else {
			builder.timeStatus(EventTimeStatus.ONGOING);
		}
	}

}

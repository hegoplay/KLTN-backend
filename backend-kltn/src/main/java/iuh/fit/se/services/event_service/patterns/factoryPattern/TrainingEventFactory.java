package iuh.fit.se.services.event_service.patterns.factoryPattern;

import java.util.HashSet;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.entity.User;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventOrganizerSingleRequestDto;
import iuh.fit.se.services.event_service.dto.request.TrainingEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.training_service.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TrainingEventFactory extends EventFactory {

	EventMapper eventMapper;
	TrainingRepository trainingRepository;

	@Override
	protected Event generateEvent(BaseEventCreateRequestDto dto) {

		TrainingEventCreateRequestDto etDto = (TrainingEventCreateRequestDto) dto;
		TrainingEvent seminar = eventMapper.toTrainingEvent(etDto);
		if (etDto.getTrainingId() != null) {
			seminar
				.setTraining(
					trainingRepository.getReferenceById(etDto.getTrainingId()));
		}

		return seminar;
	}

	@Override
	public EventDetailResponseDto toEventDetailResponseDto(Event e) {

		var dto = eventMapper.toEventDetailResponseDto((TrainingEvent) e);
		dto
			.setCategory(
				iuh.fit.se.services.event_service.dto.enumerator.EventCategory.TRAINING_EVENT);
		return dto;
	}

	@Override
	protected Event handleUpdateEvent(Event e, EventUpdateRequestDto dto) {
		TrainingEvent trainingEvent = (TrainingEvent) e;
		eventMapper.updateEventFromDto(dto, trainingEvent);
		return trainingEvent;
	}

	@Override
	protected void addNewOrganizerToEvent(
		Event event,
		EventOrganizerSingleRequestDto req,
		User organizerUser
	) {
		TrainingEvent trainingEvent = (TrainingEvent) event;
		Training training = trainingEvent.getTraining();

		if (!training.getMentors().contains(organizerUser)) {
			throw new IllegalArgumentException(
				"The organizer must be a mentor of the associated training");
		}
		EventOrganizer organizer = EventOrganizer
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
	}

}

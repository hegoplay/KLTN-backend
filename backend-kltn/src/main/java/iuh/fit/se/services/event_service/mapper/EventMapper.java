package iuh.fit.se.services.event_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.TrainingEvent;
import iuh.fit.se.services.event_service.dto.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {EventOrganizerMapper.class, UserMapper.class})
public abstract class EventMapper {
	
	public abstract Seminar toSeminar(EventCreateRequestDto dto);
	public abstract Contest toContest(EventCreateRequestDto dto);
	// Add other event types as needed
//	bổ sung phần after mapping để set training sau
	public abstract TrainingEvent toTrainingEvent(EventCreateRequestDto dto);
	
	public abstract EventDetailResponseDto toEventDetailResponseDto(Seminar event);
	public abstract EventDetailResponseDto toEventDetailResponseDto(Contest event);
	public abstract EventDetailResponseDto toEventDetailResponseDto(TrainingEvent event);
	
	@AfterMapping
	public void afterEventDetailResponseDto(@MappingTarget EventDetailResponseDto.EventDetailResponseDtoBuilder builder, TrainingEvent event) {
		if (event.getTraining() != null) {
			builder.trainingId(event.getTraining().getId());
		}
	}
	
}


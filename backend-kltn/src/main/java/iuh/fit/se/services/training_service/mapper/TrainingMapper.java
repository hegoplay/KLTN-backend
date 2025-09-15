package iuh.fit.se.services.training_service.mapper;

import java.util.ArrayList;
import java.util.HashSet;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import iuh.fit.se.entity.Training;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.training_service.dto.TrainingCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingPatchRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class})
public abstract class TrainingMapper {

	@Mapping(target = "trainingEvents", ignore = true)
	public abstract Training toTraining(TrainingCreateRequestDto dto);

	public abstract TrainingWrapperDto toTrainingWrapperDto(Training training);
	public abstract TrainingDetailDto toTrainingDetailDto(Training training);

	@AfterMapping
	public void afterMappingToTraining(
		TrainingCreateRequestDto dto,
		@MappingTarget Training.TrainingBuilder training
	) {
		if (dto.getMentorIds() == null) {
			training.mentors(new HashSet<>());
		}
		training.trainingEvents(new java.util.ArrayList<>());

	}

	@BeanMapping(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract void updateTrainingFromDtoNullIgnore(
		TrainingPatchRequestDto dto,
		@MappingTarget Training training
	);
}

package iuh.fit.se.services.training_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import iuh.fit.se.common.dto.SearchDto;
import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.training_service.dto.TrainingCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingEventListCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingPatchRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingSearchDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;

public interface TrainingService {
	TrainingDetailDto createTraining(TrainingCreateRequestDto dto);

	Page<TrainingWrapperDto> getMyTrainings(
		TrainingSearchDto dto,
		FunctionStatus status
	);
	Page<TrainingWrapperDto> getPublicTrainings(TrainingSearchDto dto);
	Page<TrainingWrapperDto> getAllTrainings(
		TrainingSearchDto dto,
		FunctionStatus status
	);
	Page<TrainingWrapperDto> getRegisteredTrainings(
		TrainingSearchDto dto
		);
	Page<TrainingWrapperDto> searchTrainings(
		Specification<Training> spec,
		TrainingSearchDto dto
	);
	
	Page<UserShortInfoResponseDto> getTrainingParticipants(
		String trainingId,
		SearchDto dto
	);

	void deleteTraining(String trainingId);
	void changeTrainingStatus(String trainingId, FunctionStatus status);
	void registerTraining(String trainingId, String userId);
	void selfRegisterTraining(String trainingId);
	void unregisterTraining(String trainingId, String userId);
	void selfUnregisterTraining(String trainingId);
	void selfTriggerRegisterTraining(String trainingId);
	void manualTriggerRegisterTraining(
		String trainingId,
		java.util.List<String> userIds
	);

	boolean checkIfUserRegistered(String trainingId, String userId);

	TrainingDetailDto getTrainingById(String trainingId);
	TrainingDetailDto getPublicTrainingById(String trainingId);
	TrainingDetailDto getMyTrainingById(String trainingId);

	TrainingDetailDto insertTrainingEvents(
		String trainingId,
		TrainingEventListCreateRequestDto dto
	);

	TrainingDetailDto updateWrapperInformation(
		String trainingId,
		TrainingPatchRequestDto dto
	);
	void updateTrainingStatus(String trainingId, FunctionStatus status);
	
	void patchTrainingMentors(
		String trainingId,
		java.util.List<String> addingMentorIds,
		java.util.List<String> removingMentorIds
	);
	
	void updateTrainingMentors(
		String trainingId,
		java.util.List<String> mentorIds
	);
}

package iuh.fit.se.services.training_service.dto;

import iuh.fit.se.entity.enumerator.FunctionStatus;

public record TrainingStatusRequestDto(
	FunctionStatus status) {

}

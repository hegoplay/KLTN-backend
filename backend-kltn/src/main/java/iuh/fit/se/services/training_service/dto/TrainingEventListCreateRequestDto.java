package iuh.fit.se.services.training_service.dto;

import java.util.List;

import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;

@lombok.Value
public class TrainingEventListCreateRequestDto {
	List<BaseEventCreateRequestDto> events;	
	
}

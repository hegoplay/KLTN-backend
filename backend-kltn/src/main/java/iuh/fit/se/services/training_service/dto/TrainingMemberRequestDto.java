package iuh.fit.se.services.training_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class TrainingMemberRequestDto {
	List<String> addMentorIds;
	List<String> removeMentorIds;
}

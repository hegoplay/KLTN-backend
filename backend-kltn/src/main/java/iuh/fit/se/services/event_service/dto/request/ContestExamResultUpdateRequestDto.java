package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ContestExamResultUpdateRequestDto {
	@NotNull
	List<ExamResultRequestDto> examResults;
}

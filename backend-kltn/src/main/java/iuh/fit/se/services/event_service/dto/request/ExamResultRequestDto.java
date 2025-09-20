package iuh.fit.se.services.event_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExamResultRequestDto(
	@NotNull String studentId,
	@NotNull @Min(
		value = 1, message = "Rank must be greater than 0") Integer rank,
	@Min(value = 0, message = "Point ít nhất 0 điểm") Integer point) {

}

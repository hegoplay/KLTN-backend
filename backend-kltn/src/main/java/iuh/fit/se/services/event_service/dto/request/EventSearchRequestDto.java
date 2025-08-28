package iuh.fit.se.services.event_service.dto.request;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;

@Schema(description = "DTO dùng để tìm kiếm sự kiện với các tiêu chí khác nhau")
public record EventSearchRequestDto(
	@Schema(
		description = "Từ khóa tìm kiếm trong tiêu đề hoặc mô tả sự kiện",
		example = "Java") String keyword,
	@Schema(
		description = "Loại sự kiện để lọc kết quả",
		example = "SEMINAR",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED) EventSearchType type, // Enum:
																				// SEMINAR,
																				// CONTEST,
																				// TRAINING
	@Schema(
		description = "Kiểm tra sự kiện đã hoàn thành hay chưa",
		example = "false",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED) Boolean isDone,
	
	LocalDateTime startTime,
	LocalDateTime endTime,
	int page,
	int size,
	Sort sortBy
) {
	@Schema(hidden = true)
	public Pageable toPageable() {
		return PageRequest.of(page, size, sortBy);
	}
}

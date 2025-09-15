package iuh.fit.se.services.training_service.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
public class TrainingSearchDto {
	@Schema(
		description = "Từ khóa tìm kiếm trong tiêu đề hoặc mô tả sự kiện",
		example = "Java") String keyword;
	LocalDateTime startTime;
	LocalDateTime endTime;
	int page;
	int size;
	Sort sortBy;
	@Schema(hidden = true)
	public Pageable toPageable() {
		if (sortBy == null) {
			sortBy = Sort.by("createdAt").descending();
		}
		return PageRequest.of(page, size, sortBy);
	}
}

package iuh.fit.se.services.training_service.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

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
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime startTime;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime endTime;
	
	@Schema(description = "Số trang hiện tại (bắt đầu từ 0)", example = "0")
	@Builder.Default
	int page = 0;
	@Schema(description = "Kích thước trang (số lượng kết quả trên một trang)", example = "10")
	@Builder.Default
	int size = 10;
	@Schema(description = "Tiêu chí sắp xếp kết quả", example = "createdAt,desc")
	@Builder.Default
	String sort = "createdAt,desc";
	@Schema(hidden = true)
	Sort sortBy;
	@Schema(hidden = true)
	public Pageable toPageable() {
		if (sortBy == null) {
			sortBy = Sort.by("createdAt").descending();
		}
		return PageRequest.of(page, size, sortBy);
	}
}

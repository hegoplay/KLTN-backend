package iuh.fit.se.services.event_service.dto.request;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Schema(description = "DTO dùng để tìm kiếm sự kiện với các tiêu chí khác nhau")
@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class EventSearchRequestDto{
	@Schema(
		description = "Từ khóa tìm kiếm trong tiêu đề hoặc mô tả sự kiện",
		example = "Java") String keyword;
	@Schema(
		description = "Loại sự kiện để lọc kết quả",
		example = "SEMINAR",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED) 
	@Builder.Default
	EventSearchType type = EventSearchType.ALL; // Enum:
																				// SEMINAR,
																				// CONTEST,
																				// TRAINING
	@Schema(
		description = "Kiểm tra sự kiện đã hoàn thành hay chưa",
		example = "false",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED) Boolean isDone;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime startTime;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime endTime;
	@Builder.Default
	int page = 0;
	@Builder.Default
	@Schema(
		description = "Kích thước trang (số lượng kết quả trên một trang)",
		example = "10")
	int size = 10;
	@Builder.Default
	@Schema(
		description = "Tiêu chí sắp xếp kết quả",
		example = "location.startTime,asc")
	String sort = "location.startTime,asc";
	@Schema(hidden = true)
	Sort sortBy;
	@Schema(hidden = true)
	public Pageable toPageable() {
		return PageRequest.of(page, size, sortBy);
	}
}

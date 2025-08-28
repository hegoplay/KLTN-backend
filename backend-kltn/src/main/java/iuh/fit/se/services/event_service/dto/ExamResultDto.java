package iuh.fit.se.services.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(
	description = "DTO dùng để trả về thông tin kết quả thi của người tham dự sự kiện")
public class ExamResultDto {
	@Schema(description = "Mã định danh của kết quả thi", example = "uuid")
	String id;
	@Schema(
		description = "Thông tin ngắn gọn của thí sinh",
		requiredMode = Schema.RequiredMode.REQUIRED)
	UserShortInfoResponseDto student;
	@Schema(description = "Xếp hạng của thí sinh trong kỳ thi", example = "1")
	Integer rank;
	@Schema(description = "Điểm số của thí sinh. Sẽ được nhân lên vs hệ số multiple trước đó", example = "8")
	Integer point;
}

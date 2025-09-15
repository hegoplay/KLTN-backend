package iuh.fit.se.services.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.enumerator.EventTimeStatus;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
@Schema(description = "DTO dùng để trả về thông tin cơ bản của sự kiện")
@ToString
public class EventWrapperDto {
	@EqualsAndHashCode.Include
	@Schema(description = "event_id", example = "event uuid"
	// requiredMode = Schema.RequiredMode.REQUIRED
	)
	String id;
	@Schema(
		description = "Thông tin người tạo sự kiện",
		requiredMode = Schema.RequiredMode.REQUIRED)
	UserShortInfoResponseDto host;
	@Schema(
		description = "Địa điểm tổ chức sự kiện",
		requiredMode = Schema.RequiredMode.REQUIRED)
	LocationDto location;
	@Schema(
		description = "Tiêu đề của sự kiện",
		example = "Hội thảo khoa học quốc tế về AI")
	String title;
	@Schema(
		description = "Mô tả chi tiết về sự kiện",
		example = "Hội thảo này sẽ tập trung vào các ứng dụng mới nhất của trí tuệ nhân tạo trong các lĩnh vực khác nhau.")
	String description;
	@Schema(
		description = "Hệ số nhân điểm cho sự kiện (ít nhất là 1)",
		example = "1")
	Integer multiple;
	@Schema(
		description = "Trạng thái hiện tại của sự kiện",
		example = "PENDING")
	FunctionStatus status;
	@Schema(description = "Trạng thái kiểm tra sự kiện đã kết thúc chưa")
	Boolean done;
	@Schema(description = "Trạng thái thời gian của sự kiện (UPCOMING, ONGOING, COMPLETED, LOCKED)")
	EventTimeStatus timeStatus;
	@Schema(description = "Loại sự kiện (SEMINAR, TRAINING, CONTEST)")
	EventCategory category;
}

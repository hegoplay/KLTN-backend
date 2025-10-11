package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.validation.constraints.Min;

public record EventUpdateRequestDto(
	@Schema(
		description = "Tiêu đề của sự kiện",
		example = "Hội thảo về công nghệ mới",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String title,
	@Schema(
		description = "Địa điểm tổ chức và thời gian diễn ra của sự kiện",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	LocationDto location,
	@Schema(
		description = "Nội dung chi tiết về sự kiện",
		example = "Sự kiện này sẽ tập trung vào các xu hướng mới nhất trong lĩnh vực Công nghệ Thông tin...",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String description,
	@Min(1)
	@Schema(
		description = "Hệ số nhân điểm cho sự kiện (ít nhất là 1)",
		example = "1",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer multiple,
	
	@Schema(description = "Giới hạn số người đăng ký tham gia sự kiện, nếu không có giới hạn thì để null",
		example = "100",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	Integer limitRegister,
	@Schema(
		description = "Trạng thái chức năng của sự kiện sau khi cập nhật. Chỉ được phép PENDING hoặc ARCHIVED",
		example = "PENDING",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	FunctionStatus status
) {

}

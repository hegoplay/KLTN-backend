package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "DTO dùng để tạo mới sự kiện")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SingleEventCreateRequestDto extends BaseEventCreateRequestDto {

	@Schema(description = "Số lượng tối đa người tham gia", example = "100")
	@Min(value = 0, message = "Số lượng tối đa người tham gia phải lớn hơn = 0")
	private Integer limitRegister;
	
	@Schema(
		description = "Trạng thái lúc khởi tạo sự kiện (Chỉ được là PENDING hoặc ARCHIVED)",
		example = "PENDING",
		requiredMode = RequiredMode.NOT_REQUIRED)
	private FunctionStatus status;

	@Schema(description = "Loại sự kiện", defaultValue = "SEMINAR")
	@NotNull(message = "Loại sự kiện không được để trống")
	private EventCategory category;
	
	@Schema(hidden = true)
	public boolean isCreateAble() {
		return this.status == null || this.status == FunctionStatus.PENDING
			|| this.status == FunctionStatus.ARCHIVED;
	}
}
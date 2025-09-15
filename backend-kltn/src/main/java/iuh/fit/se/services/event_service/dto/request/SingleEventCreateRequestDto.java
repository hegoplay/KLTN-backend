package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.entity.enumerator.FunctionStatus;
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

	
	@Schema(
		description = "Trạng thái lúc khởi tạo sự kiện (Chỉ được là PENDING hoặc ARCHIVED)",
		example = "PENDING",
		requiredMode = RequiredMode.NOT_REQUIRED)
	private FunctionStatus status;

	@Schema(hidden = true)
	public boolean isCreateAble() {
		return this.status == null || this.status == FunctionStatus.PENDING
			|| this.status == FunctionStatus.ARCHIVED;
	}
}
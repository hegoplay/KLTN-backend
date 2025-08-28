package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.OrganizerRole;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO dùng để thêm/sửa/xóa người tổ chức sự kiện")
public record EventOrganizerSingleRequestDto(
	@Schema(
		description = "Mã định danh của người tổ chức",
		example = "uuid user",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank
	String organizerId,
	@Schema(
		description = "Chú thích nội dung vai trò của người tổ chức",
		example = "Người điểm danh",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String roleContent,
	@Schema(
		description = "Danh sách quyền của người tổ chức",
		example = "[\"MODIFY\", \"CHECK_IN\"]",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<OrganizerRole> roles,
	@Schema(
		description = "Tham số kiểm tra người tổ chức có bị xóa khỏi sự kiện hay không, nếu không có @thì mặc định là false",
		example = "false",
		defaultValue = "false"
	)
	@JsonProperty(defaultValue = "false") boolean removed
) {
}

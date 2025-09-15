package iuh.fit.se.services.training_service.dto;

import org.hibernate.validator.constraints.Length;

import com.esotericsoftware.kryo.serializers.FieldSerializer.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.common.enumerator.RequestFunctionStatus;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO cho việc cập nhật thông tin cơ bản của khóa học")
public record TrainingPatchRequestDto(
	@Schema(
		description = "Tiêu đề của khóa học",
		example = "Khóa học Quarkus nâng cao")
	@Length(min = 8, message = "Tiêu đề phải có ít nhất 8 ký tự") String title,
	@Schema(description = "Địa điểm tổ chức khóa học") LocationDto location,
	@Schema(
		description = "Mô tả về khóa học",
		example = "Khóa học về Quarkus nâng cao dành cho các lập trình viên Java")
	@Length(
		min = 20,
		message = "Mô tả phải có ít nhất 20 ký tự") String description,
	@Schema(
		description = "Trạng thái yêu cầu cho khóa học (PENDING, ARCHIVED)",
		example = "PENDING",
		requiredMode = RequiredMode.REQUIRED)
	@NotNull
	@NotBlank RequestFunctionStatus status

) {

}

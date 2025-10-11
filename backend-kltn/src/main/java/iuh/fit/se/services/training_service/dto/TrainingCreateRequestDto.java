package iuh.fit.se.services.training_service.dto;

import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import com.esotericsoftware.kryo.serializers.FieldSerializer.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldDefaults;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Data
@lombok.Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(description = "DTO for creating a new training")
public class TrainingCreateRequestDto {
	@Schema(
		description = "Tiêu đề khóa học",
		example = "Java Programming Basics")
	@NotNull
	@NotBlank
	@Length(min = 8, message = "Tiêu đề phải có ít nhất 8 ký tự")
	String title;
	@Schema(description = "Danh sách sự kiện thuộc khóa học")
	List<BaseEventCreateRequestDto> trainingEvents;
	@Schema(description = "Địa điểm tổ chức khóa học")
	LocationDto location;
	@Schema(
		description = "Mô tả về khóa học",
		example = "Khóa học này giúp bạn nắm vững các kiến thức cơ bản về lập trình Java.")
	@NotNull
	@Length(min = 20, message = "Mô tả phải có ít nhất 20 ký tự")
	String description;
	@Schema(
		description = "Danh sách ID của các mentor tham gia giảng dạy trong khóa học",
		example = "[\"mentorId1\", \"mentorId2\"]")
	@UniqueElements(
		message = "Danh sách mentorIds không được chứa phần tử trùng lặp")
	Set<String> mentorIds;
	
	@Schema(
		description = "Giới hạn số lượng người đăng ký tham gia khóa học",
		example = "100",
		requiredMode = RequiredMode.NOT_REQUIRED)
	@Min(value = 0, message = "Giới hạn đăng ký phải lớn hơn = 0")
	Integer limitRegister;
	
	@Schema(
		description = "Trạng thái của khóa học (có thể là ACCEPTED hoặc REJECTED)",
		example = "ACCEPTED")
	FunctionStatus status;

}

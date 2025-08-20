package iuh.fit.se.services.post_service.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SearchPostRequestDto {
	@NotBlank
	@Length(min = 3, max = 100, message = "searchKeyword phải có độ dài từ 3 đến 100 ký tự")
	@Schema(description = "Từ khóa tìm kiếm trong tiêu đề hoặc nội dung bài viết", example = "Hướng dẫn lập trình Java", minLength = 3, maxLength = 100)
	String searchKeyword;

	@Builder.Default
	@Schema(description = "Ngày bắt đầu tìm kiếm bài viết", example = "1970-01-01", type = "string", format = "date")
	LocalDate fromDate = LocalDate.of(1970, 1, 1); // Default to Unix epoch
													// start;
	@Builder.Default
	@Schema(description = "Ngày kết thúc tìm kiếm bài viết", example = "9999-12-31", type = "string", format = "date")
	LocalDate toDate = LocalDate.now(); // Default to today
}

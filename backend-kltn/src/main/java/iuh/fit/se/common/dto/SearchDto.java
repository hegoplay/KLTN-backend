package iuh.fit.se.common.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO search các thuộc tính")
public class SearchDto {
//	@Schema(description = "Từ khóa tìm kiếm")
//	String keyword;
//
//	@Schema(description = "Ngày bắt đầu")
//	LocalDateTime startDate;
//
//	@Schema(description = "Ngày kết thúc")
//	LocalDateTime endDate;
	@Schema(description = "Số trang hiện tại", defaultValue = "0")
	Integer page;
	@Schema(description = "Số phần tử trên một trang", defaultValue = "10")
	Integer size;
	@Schema(description = "Thuộc tính sắp xếp. template: createdAt, asc hoặc desc", defaultValue = "createdAt,desc")
	String sort;
	
	
	public Pageable toPageable() {
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size <= 0) ? 10 : size;
		
		String sortBy = null;
		String sort = null;
		
		if (this.sort != null && !this.sort.isEmpty()) {
			String[] parts = this.sort.split(",");
			if (parts.length == 2) {
				sortBy = parts[0];
				sort = parts[1];
			} else if (parts.length == 1) {
				sortBy = parts[0];
			}
		}
		String sb = (sortBy == null || sortBy.isEmpty()) ? "createdAt" : sortBy;
		String st = (sort == null || sort.isEmpty() || (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc"))) ? "desc" : sort;
		org.springframework.data.domain.Sort sortOrder = st.equalsIgnoreCase("asc") ?
			org.springframework.data.domain.Sort.by(sb).ascending() :
			org.springframework.data.domain.Sort.by(sb).descending();
		return org.springframework.data.domain.PageRequest.of(p, s, sortOrder);
	}
	
}

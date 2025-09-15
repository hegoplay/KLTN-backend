package iuh.fit.se.services.training_service.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingSearchDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.training_service.service.TrainingService;
import iuh.fit.se.util.PageableUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/public/trainings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Validated
@Tag(
	name = "Training Public Management",
	description = """
		API này hỗ trợ các api truy cập công khai không yêu cầu JWT.
		Ngoài ra training mà trả về có status là ACCEPTED (được duyệt) mới được trả về.
		""")
public class TrainingPublicController {

	TrainingService trainingService;
	PagedResourcesAssembler<TrainingWrapperDto> trainingPagedResourcesAssembler;

	@Operation(
		summary = "Tìm kiếm các khóa học công khai",
		description = """
			API này cho phép người dùng tìm kiếm các khóa học công khai dựa trên từ khóa,
			khoảng thời gian bắt đầu và kết thúc.
			Kết quả trả về là danh sách các khóa học được phân trang.
			""")
	@GetMapping("/search")
	public ResponseEntity<PagedModel<EntityModel<TrainingWrapperDto>>> getMyTrainings(
		@RequestParam(required = false) String keyword,

		@Schema(
			description = "Thời gian bắt đầu để lọc sự kiện (ISO format)",
			example = "2024-01-01T00:00:00")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime startTime,

		@Schema(
			description = "Thời gian kết thúc để lọc sự kiện (ISO format)",
			example = "2025-12-31T23:59:59")
		@RequestParam(required = false)
		@DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "100", required = false) int size,
		@RequestParam(defaultValue = "title,asc", required = false) String sort

	) {
		TrainingSearchDto searchDto = TrainingSearchDto
			.builder()
			.keyword(keyword)
			.startTime(startTime)
			.endTime(endTime)
			.keyword(keyword)
			.page(page)
			.size(size)
			.sortBy(PageableUtil.parseSort(sort))
			.build();
		Page<TrainingWrapperDto> myTrainings = trainingService
			.getPublicTrainings(searchDto);
		return ResponseEntity
			.ok(trainingPagedResourcesAssembler.toModel(myTrainings));
	}

	@Operation(summary = "Lấy chi tiết khóa học công khai", description = """
		API này cho phép người dùng lấy chi tiết của một khóa học công khai
		dựa trên ID của khóa học.
		""")
	@GetMapping("/{trainingId}")
	public ResponseEntity<TrainingDetailDto> getTrainingDetail(
		@PathVariable String trainingId
	) {
		return ResponseEntity
			.ok(trainingService.getPublicTrainingById(trainingId));
	}

}

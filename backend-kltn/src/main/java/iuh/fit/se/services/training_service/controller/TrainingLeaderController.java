package iuh.fit.se.services.training_service.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingSearchDto;
import iuh.fit.se.services.training_service.dto.TrainingStatusRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.training_service.service.TrainingService;
import iuh.fit.se.util.PageableUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/leader/trainings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Validated
@Tag(
	name = "Training Leader Management",
	description = "API nãy hỗ trợ các api quản lý bài truyền thông CUD (Create, Update, Delete) và các api yêu cầu JWT")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden")})
public class TrainingLeaderController {

	TrainingService trainingService;
	PagedResourcesAssembler<TrainingWrapperDto> trainingPagedResourcesAssembler;

	@GetMapping("/search")
	@Operation(summary = "Lấy danh sách các training", description = """
		API trả về toàn bộ training đang tồn tại trong hệ thống
		""")
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
		@RequestParam(required = false) FunctionStatus status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "100") int size,
		@RequestParam(defaultValue = "title,asc") String sort

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
		Page<TrainingWrapperDto> allTrainings = trainingService
			.getAllTrainings(searchDto, status);
		return ResponseEntity
			.ok(trainingPagedResourcesAssembler.toModel(allTrainings));
	}

	@GetMapping("/{trainingId}")
	@Operation(
		summary = "Lấy chi tiết một training",
		description = "Trả về chi tiết một training bất kỳ")
	public ResponseEntity<TrainingDetailDto> getTrainingDetail(
		@PathVariable String trainingId
	) {
		return ResponseEntity.ok(trainingService.getTrainingById(trainingId));
	}

	@ApiResponses({
			@ApiResponse(
				responseCode = "204",
				description = "Xóa training thành công"),
			@ApiResponse(
				responseCode = "404",
				description = "Không tìm thấy training với ID đã cho"),
			@ApiResponse(
				responseCode = "400",
				description = "Yêu cầu không hợp lệ, có thể training event đã được tính điểm"),
			@ApiResponse(
				responseCode = "500",
				description = "Lỗi server nội bộ")})
	@Operation(
		summary = "Xóa một training",
		description = "Xóa một training bất kỳ dựa trên ID của nó")
	@DeleteMapping("/{trainingId}")
	public ResponseEntity<Void> deleteTraining(
		@RequestParam String trainingId
	) {
		trainingService.deleteTraining(trainingId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{trainingId}/status")
	@ApiResponses({@ApiResponse(
		responseCode = "202",
		description = "Yêu cầu cập nhật trạng thái training đã được chấp nhận"),
			@ApiResponse(
				responseCode = "400",
				description = "Yêu cầu không hợp lệ"),
			@ApiResponse(
				responseCode = "404",
				description = "Không tìm thấy training với ID đã cho"),
			@ApiResponse(
				responseCode = "500",
				description = "Lỗi server nội bộ")})
	@Operation(
		summary = "Cập nhật trạng thái của một training",
		description = """
			API này cho phép cập nhật trạng thái của một training
			
			các trạng thái có thể là ACCEPTED, là 1 api ngắn gọn để duyệt training nhanh 
			""")
	public ResponseEntity<Void> updateTrainingStatus(
		@PathVariable String trainingId,
		@RequestBody TrainingStatusRequestDto dto
	) {
		trainingService.updateTrainingStatus(trainingId, dto.status());
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

}

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.api.TrainingAPI;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.request.ManualTriggerRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingEventListCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingMemberRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingPatchRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingSearchDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.training_service.service.TrainingService;
import iuh.fit.se.util.PageableUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(TrainingAPI.BASE_URL)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Validated
@Tag(
	name = "Training Management",
	description = "API nãy hỗ trợ các api quản lý bài truyền thông CUD (Create, Update, Delete)"
		+ ", read các training của mình (là creator hay 1 phần của mentors) và các api yêu cầu JWT")
@SecurityRequirement(name = "bearerAuth")
public class TrainingController {

	TrainingService trainingService;
	PagedResourcesAssembler<TrainingWrapperDto> trainingPagedResourcesAssembler;

	@PostMapping
	@Operation(summary = "Tạo mới một training", description = """
		API này cho phép người dùng tạo mới một training

		chỉ cho phép tạo mới với trạng thái là ARCHIVED hoặc PENDING
		""")
	public ResponseEntity<TrainingDetailDto> createTraining(
		@RequestBody @Valid TrainingCreateRequestDto dto
	) {
		return ResponseEntity.ok(trainingService.createTraining(dto));
	}

	@GetMapping("/{trainingId}")
	@Operation(
		summary = "Lấy chi tiết một training của tôi",
		description = "Trả về chi tiết một training mà tôi là creator hoặc mentor")
	public ResponseEntity<TrainingDetailDto> getTrainingDetail(
		@PathVariable String trainingId
	) {
		return ResponseEntity.ok(trainingService.getMyTrainingById(trainingId));
	}

	@GetMapping("/me/search")
	@Operation(
		summary = "Tìm kiếm training của tôi base theo creator hoặc mentor",
		description = "Trả về danh sách các training mà tôi là creator hoặc mentor")
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
			.size(size)
			.startTime(startTime)
			.endTime(endTime)
			.keyword(keyword)
			.sortBy(PageableUtil.parseSort(sort))
			.build();
		Page<TrainingWrapperDto> myTrainings = trainingService
			.getMyTrainings(searchDto, status);
		return ResponseEntity
			.ok(trainingPagedResourcesAssembler.toModel(myTrainings));
	}

	@PatchMapping("/{trainingId}")
	@Operation(
		summary = "Cập nhật thông tin chung của một training",
		description = """
			API này cho phép cập nhật thông tin chung của một training

			trong đó status chỉ được phép cập nhật từ PENDING sang ARCHIVED và là 1 trường bắt buộc

			""")
	public ResponseEntity<TrainingDetailDto> updateTrainingWrapperInfo(
		@PathVariable String trainingId,
		@RequestBody @Valid TrainingPatchRequestDto dto
	) {
		TrainingDetailDto updateWrapperInformation = trainingService
			.updateWrapperInformation(trainingId, dto);
		return ResponseEntity.ok(updateWrapperInformation);
	}

	@Operation(summary = "Đăng ký tham ga một training", description = """
		API này cho phép người dùng tham gia vào 1 training
		""")
	@PostMapping("/{trainingId}/self-trigger-register")
	public ResponseEntity<Void> selfTriggerRegisterTraining(
		@PathVariable String trainingId
	) {
		trainingService.selfTriggerRegisterTraining(trainingId);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Đăng ký tham gia một training cho một hoặc nhiều người dùng",
		description = """
			API này cho phép người dùng đăng ký tham gia một training cho một hoặc nhiều người dùng khác

			chỉ có creator của training, mentors hoặc leader trở lên mới có quyền thực hiện hành động này

			""")
	@PostMapping("/{trainingId}/manual-trigger-register")
	public ResponseEntity<Void> manualTriggerRegisterTraining(
		@PathVariable String trainingId,
		@RequestBody @Valid ManualTriggerRequestDto dto
	) {
		trainingService
			.manualTriggerRegisterTraining(trainingId, dto.attendeeIds());
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Chèn thêm các sự kiện vào một training",
		description = """
			API này cho phép chèn thêm các sự kiện vào một training

			chỉ có creator của training hoặc leader trở lên mới có quyền thực hiện hành động này

			""")
	@PostMapping("/{trainingId}/events")
	public ResponseEntity<TrainingDetailDto> insertTrainingEvents(
		@PathVariable String trainingId,
		@RequestBody(required = true) TrainingEventListCreateRequestDto dto
	) {
		return ResponseEntity
			.ok(trainingService.insertTrainingEvents(trainingId, dto));
	}

	@PutMapping("/{trainingId}/members")
	@Operation(
		summary = "Thêm hoặc bớt thành viên (mentor, participant) cho một training",
		description = """
			API này cho phép thêm hoặc bớt thành viên (mentor, participant) cho một training

			Chỉ có creator của training hoặc leader trở lên mới có quyền thực hiện hành động này

			""")
	public ResponseEntity<Void> modifyMembersToTraining(
		@PathVariable String trainingId,
		@RequestBody @Valid TrainingMemberRequestDto dto
	) {
		trainingService
			.updateTrainingMentors(trainingId, dto.getAddMentorIds(),
				dto.getRemoveMentorIds());
		return ResponseEntity.accepted().build();
	}
	@GetMapping("/search/registered-trainings")
	@Operation(
		summary = "Tìm kiếm các training mà tôi đã đăng ký tham gia",
		description = "Trả về danh sách các training mà tôi đã đăng ký tham gia")
	public ResponseEntity<PagedModel<EntityModel<TrainingWrapperDto>>> getRegisteredTrainings(
		@ModelAttribute TrainingSearchDto searchDto
		) {
		searchDto.setSortBy(PageableUtil.parseSort(searchDto.getSort()));
		Page<TrainingWrapperDto> registeredTrainings = trainingService
			.getRegisteredTrainings(searchDto);
		return ResponseEntity
			.ok(trainingPagedResourcesAssembler.toModel(registeredTrainings));
	}
}

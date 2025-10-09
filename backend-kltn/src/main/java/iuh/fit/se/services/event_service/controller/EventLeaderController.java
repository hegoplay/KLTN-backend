package iuh.fit.se.services.event_service.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventStatusUpdateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.PageableUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/leader/events")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(
	name = "Event Leaders Management",
	description = """
		API này hỗ trợ các công việc quản lý sự kiện của leader. API chỉ cho phép truy cập khi user là leader trở lên
		""")
@SecurityRequirement(name = "bearerAuth")
public class EventLeaderController {

	// duyệt sự kiện
	EventService eventService;
	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	EventMapper eventMapper;

	@Operation(
		summary = "Tìm kiếm tất cả sự kiện",
		description = """
			API này cho phép tìm kiếm tất cả sự kiện với các tiêu chí lọc như từ khóa, loại sự kiện,
			Ý nghĩa của các tham số:
			- keyword: Từ khóa để tìm kiếm trong tiêu đề hoặc mô tả sự kiện.
			- eventType: Loại sự kiện để lọc kết quả (ví dụ: SEMINAR, WORKSHOP, MEETING, ALL).
			- isDone: Lọc sự kiện đã hoàn thành (true) hoặc chưa hoàn thành (false).
			- page: Số trang (bắt đầu từ 0) để phân trang kết quả.
			- size: Số lượng sự kiện trên mỗi trang.
			- sort: Tiêu chí sắp xếp kết quả (ví dụ: location.startTime,asc).
			- status: Trạng thái của sự kiện (PENDING, ARCHIVED, ACCEPTED, REJECTED, DISABLED).
			""")

	@GetMapping("/search")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> searchEvents(
		@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false,
			defaultValue = "ALL") EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime startTime,

		@Schema(
			description = "Thời gian kết thúc để lọc sự kiện (ISO format)",
			example = "2025-12-31T23:59:59")
		@RequestParam(required = false)
		@DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam FunctionStatus status
	) {
		EventSearchRequestDto request = new EventSearchRequestDto(keyword,
			eventType, isDone, startTime, endTime, page, size,
			sort,PageableUtil.parseSort(sort));

		Page<Event> events = eventService.searchAllEvents(request, status);
		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(events.map(eventMapper::toEventWrapperDto)));
	}

	@Operation(
		summary = "Tìm kiếm sự kiện của user",
		description = """
			API này cho phép tìm kiếm sự kiện của user với các tiêu chí lọc như từ khóa, loại sự kiện,
			Ý nghĩa của các tham số:
			- userId: Mã định danh của user.
			- keyword: Từ khóa để tìm kiếm trong tiêu đề hoặc mô tả sự kiện.
			- eventType: Loại sự kiện để lọc kết quả (ví dụ: SEMINAR, WORKSHOP, MEETING, ALL).
			- isDone: Lọc sự kiện đã hoàn thành (true) hoặc chưa hoàn thành (false).
			- page: Số trang (bắt đầu từ 0) để phân trang kết quả.
			- size: Số lượng sự kiện trên mỗi trang.
			- sort: Tiêu chí sắp xếp kết quả (ví dụ: location.startTime,asc).
			- status: Trạng thái của sự kiện (PENDING, ARCHIVED, ACCEPTED, REJECTED, DISABLED).
			""")
	@GetMapping("/user/{userId}/search")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> getEventsByUser(
		@PathVariable String userId,
		@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false,
			defaultValue = "ALL") EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@Schema(
			description = "Thời gian kết thúc để lọc sự kiện (ISO format)",
			example = "2024-00-00T23:59:59")
		@DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) 
		@RequestParam(required = false)
		LocalDateTime startTime,

		@Schema(
			description = "Thời gian kết thúc để lọc sự kiện (ISO format)",
			example = "2025-12-31T23:59:59")
		@RequestParam(required = false)
		@DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam FunctionStatus status
	) {
		EventSearchRequestDto request = new EventSearchRequestDto(keyword,
			eventType, isDone, startTime, endTime, page, size,
			sort,PageableUtil.parseSort(sort));

		Page<Event> events = eventService
			.searchUserEvents(request, null, status);
		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(events.map(eventMapper::toEventWrapperDto)));
	}

	@Operation(summary = "Xoá sự kiện", description = """
		API này cho phép xoá sự kiện theo eventId.
		""")
	@DeleteMapping("/{eventId}")
	public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
		eventService.deleteEvent(eventId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "Kích hoạt sự kiện đã hoàn thành",
		description = """
			API này cho phép kích hoạt sự kiện đã hoàn thành theo eventId.
			Nếu sự kiện đã hoàn thành, nó sẽ được đánh dấu là chưa hoàn thành.
			Sự kiện được phép kích hoạt khi nó đã ở trạng thái ACCEPTED và đã trong trạng thái kết thúc.
			(Debug: Hiện tại chỉ kiểm tra thời gian sau ngày bắt đầu)
			""")
	@PatchMapping("/{eventId}/trigger-done")
	public ResponseEntity<Void> triggerEventDone(@PathVariable String eventId) {
		eventService.triggerEventDone(eventId);
		return ResponseEntity.accepted().build();
	}

	@Operation(
		summary = "Cập nhật trạng thái sự kiện",
		description = """
			API này cho phép cập nhật trạng thái của sự kiện theo eventId.
			Ý nghĩa của các tham số:
			- eventId: Mã định danh của sự kiện cần cập nhật.
			- request: Đối tượng chứa trạng thái mới của sự kiện.
			Các trạng thái hợp lệ bao gồm: PENDING, ARCHIVED, ACCEPTED, REJECTED, DISABLED.
			Chỉ được phép cập nhật khi sự kiện chưa hoàn thành (done = false).
			API chỉ áp dụng cho event đơn lẻ, không áp dụng cho sự kiện thuộc khóa học.
			""")
	@PatchMapping("/{eventId}/status")
	public ResponseEntity<Void> updateEventStatus(
		@PathVariable String eventId,
		@RequestBody EventStatusUpdateRequestDto request
	) {
		eventService.updateSingleEventStatus(eventId, request.status());
		return ResponseEntity.accepted().build();
	}

	
	
}

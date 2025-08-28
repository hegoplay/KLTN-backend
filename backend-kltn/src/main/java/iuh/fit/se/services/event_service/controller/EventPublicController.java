package iuh.fit.se.services.event_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.JwtTokenUtil;
import iuh.fit.se.util.PageableUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(name = "Event Public Management", description = """
	API này hỗ trợ các công việc liên quan đến sự kiện mà không cần xác thực.
	""")
public class EventPublicController {

	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	EventService eventService;
	EventMapper eventMapper;
	JwtTokenUtil jwtTokenUtil;

	@Operation(
		summary = "Tìm kiếm các sự kiện ACCEPTED",
		description = """
			API này cho phép tìm kiếm tất cả sự kiện với các tiêu chí lọc như từ khóa, loại sự kiện,
			Kết quả trả về là danh sách các sự kiện ở trạng thái ACCEPTED và được nhúng vào trong _embedded
			Mỗi EventWrapperDto chứa thông tin tóm tắt về sự kiện.
			""")
	@GetMapping("/search")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> searchEvents(
		@Schema(
			description = "Từ khóa tìm kiếm trong tiêu đề hoặc mô tả sự kiện",
			example = "Java")
		@RequestParam(required = false) String keyword,
		@Schema(
			description = "Loại sự kiện để lọc kết quả",
			example = "SEMINAR",
			requiredMode = Schema.RequiredMode.NOT_REQUIRED,
			implementation = EventSearchType.class)
		@RequestParam(
			required = false,
			defaultValue = "ALL") EventSearchType eventType,
		@Schema(
			description = "Kiểm tra sự kiện đã hoàn thành hay chưa",
			example = "false",
			requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		@RequestParam(required = false) Boolean isDone,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@Schema(description = """
			Các trường sắp xếp theo định dạng: "field,asc|desc".
			Mặc định là "location.startTime,asc".
			""", example = "location.startTime,asc")
		@RequestParam(defaultValue = "location.startTime,asc") String sort
	) {
		EventSearchRequestDto request = new EventSearchRequestDto(keyword,
			eventType, isDone, page, size, PageableUtil.parseSort(sort));

		Page<Event> events = eventService.searchPublicEvents(request);
		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(events.map(eventMapper::toEventWrapperDto)));
	}

	@Operation(
		summary = "Lấy chi tiết sự kiện công khai theo ID",
		description = """
			API này cho phép lấy chi tiết của một sự kiện công khai dựa trên ID của nó.
			Nếu có token hợp lệ trong header Authorization, API sẽ trả về thông tin chi tiết
			của sự kiện cùng với trạng thái tham gia của user (nếu user đã đăng ký tham gia sự kiện).
			Nếu không có token hoặc token không hợp lệ, API chỉ trả về thông tin chi tiết của sự kiện.
			Lưu ý: Chỉ các sự kiện ở trạng thái ACCEPTED mới được xem là công khai và có thể truy cập qua API này.
			Nếu sự kiện không ở trạng thái này, API sẽ trả về lỗi.
			""")
	@GetMapping("/{eventId}")
	public ResponseEntity<EventDetailResponseDto> getEventById(
		@PathVariable String eventId,
		HttpServletRequest request
	) {
		String tokenFromRequest = jwtTokenUtil.getTokenFromRequest(request);
		EventDetailResponseDto event;
		if (tokenFromRequest == null) {
			event = eventService.getEventById(eventId);
		} else {
			String userId = jwtTokenUtil.getUserIdFromToken(tokenFromRequest);
			event = eventService.getEventByIdAndUserId(eventId, userId);
		}
		if (event.getStatus() != FunctionStatus.ACCEPTED)
			throw new RuntimeException("Event is not public");
		return ResponseEntity.ok(event);
	}

}

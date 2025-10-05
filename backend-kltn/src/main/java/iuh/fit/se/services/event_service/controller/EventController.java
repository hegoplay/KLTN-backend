package iuh.fit.se.services.event_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
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
import iuh.fit.se.api.EventAPI;
import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.AttendeeDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventOrganizerDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.CheckInRequestDto;
import iuh.fit.se.services.event_service.dto.request.ContestExamResultUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.ListEventOrganizerRequestDto;
import iuh.fit.se.services.event_service.dto.request.ManualTriggerRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.response.EventCodeResponseDto;
import iuh.fit.se.services.event_service.dto.response.SeminarReviewsResponseDto;
import iuh.fit.se.services.event_service.mapper.EventAttendeeMapper;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.mapper.EventOrganizerMapper;
import iuh.fit.se.services.event_service.service.EventAttendeeService;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.ContextUtil;
import iuh.fit.se.util.JwtTokenUtil;
import iuh.fit.se.util.PageableUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EventAPI.BASE_URL)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(
	name = "Event Management",
	description = """
		API này hỗ trợ các công việc quản lý sự kiện (một số cái không hỗ trợ cho event đi theo bộ và lấy thông tin sự kiện).
		""")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

	EventService eventService;
	EventAttendeeService eventAttendeeService;

	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	PagedResourcesAssembler<AttendeeDto> attendeePagedResourcesAssembler;

	EventMapper eventMapper;
	EventAttendeeMapper eventAttendeeMapper;
	EventOrganizerMapper eventOrganizerMapper;

	JwtTokenUtil jwtT;

	@GetMapping("/{eventId}")
	@Operation(
		summary = "Lấy thông tin chi tiết của một sự kiện",
		description = """
			API này cho phép lấy thông tin chi tiết của một sự kiện dựa trên eventId.
			Nếu có token hợp lệ trong request, API sẽ trả về thông tin chi tiết bao gồm cả trạng thái tham gia của người dùng hiện tại.
			Nếu không có token hoặc token không hợp lệ, API sẽ trả về thông tin chi tiết của sự kiện mà không bao gồm trạng thái tham gia của người dùng.
			Token được gọi là hợp lệ khi là host hoặc organizer của sự kiện hoặc có vai trò là leader.
			""")
	public ResponseEntity<EventDetailResponseDto> getEventById(
		@PathVariable String eventId, HttpServletRequest request) {
		String tokenFromRequest = jwtT.getTokenFromRequest(request);
		EventDetailResponseDto event;
		if (tokenFromRequest == null) {
			event = eventService.getEventById(eventId);

		}
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		event = eventService.getEventByIdAndUserId(eventId, userId);
		if (!ContextUtil.isLeader()
			&& !event.getHost().getId().equalsIgnoreCase(userId)
			&& !event
				.getOrganizers()
				.stream()
				.anyMatch(
					o -> o.getOrganizer().getId().equalsIgnoreCase(userId))) {
			throw new RuntimeException(
				"You do not have permission to view this event");
		}
		event
			.setIsHost(Boolean.valueOf(event.getHost().getId().equals(userId)));
		return ResponseEntity.ok(event);
	}

	@PostMapping
	@Operation(
		summary = "Tạo sự kiện mới",
		description = """
				API này cho phép tạo một sự kiện đơn mới.
				Người tạo sự kiện sẽ được thể hiện ở trên host
				Trạng thái của sự kiện khi tạo chỉ được phép là PENDING hoặc ARCHIVED.
				Người tạo sự kiện phải có vai trò là member trở lên.
			""")
	public ResponseEntity<EventDetailResponseDto> createEvent(
		@RequestBody @Valid SingleEventCreateRequestDto dto) {
		EventDetailResponseDto createdEvent = eventService.createEvent(dto);
		return ResponseEntity.ok(createdEvent);
	}

	@GetMapping(EventAPI.ME_SEARCH)
	@Operation(
		summary = "Tìm kiếm sự kiện của người dùng hiện tại",
		description = """
			API này cho phép tìm kiếm các sự kiện mà người dùng hiện tại là host hoặc organizer
			dựa trên các tiêu chí như từ khóa, loại sự kiện, trạng thái hoàn thành và trạng thái chức năng.
			Kết quả trả về được phân trang và sắp xếp theo yêu cầu.
			Người dùng phải có vai trò là member trở lên để sử dụng API này.
			""")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> getEventsByUser(
		@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false, defaultValue = "ALL") EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@Schema(
			description = "Thời gian bắt đầu để lọc sự kiện (ISO format)",
			example = "2026-01-01T00:00:00") @DateTimeFormat(
				iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(
					required = false) LocalDateTime startTime,
		@Schema(
			description = "Thời gian kết thúc để lọc sự kiện (ISO format)",
			example = "2026-12-31T23:59:59") @RequestParam(
				required = false) @DateTimeFormat(
					iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam(required = false) FunctionStatus status,
		HttpServletRequest httpServletRequest) {
		EventSearchRequestDto request = new EventSearchRequestDto(keyword,
			eventType, isDone, startTime, endTime, page, size, sort,
			PageableUtil.parseSort(sort));
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		Page<Event> searchUserEvents = eventService
			.searchMyEvents(request, status, tokenFromRequest);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(searchUserEvents.map(eventMapper::toEventWrapperDto)));
	}

	@Operation(
		summary = "Lấy mã Code của sự kiện",
		description = """
			Mã code do ban tổ chức tạo và có hiệu lực trong 10 phút kể từ thời điểm tạo
			Nếu code cũ còn hiệu lực, hệ thống sẽ trả về code cũ
			Nếu code cũ đã hết hiệu lực hoặc chưa từng tạo, hệ thống sẽ tạo code mới và trả về
			Chức năng này chỉ dành cho host hoặc organizer của sự kiện
			""")
	@GetMapping("/{eventId}/code")
	public ResponseEntity<EventCodeResponseDto> getEventOTP(
		@PathVariable String eventId, HttpServletRequest httpServletRequest) {
		String code = eventService.getEventCode(eventId);
		return ResponseEntity.ok(new EventCodeResponseDto(code));
	}

	@PostMapping("/{eventId}/self-trigger-register")
	@Operation(summary = "Cá nhân tự đăng ký sự kiện", description = """
		Người dùng tự đăng ký cho sự kiện công khai
		Nếu người dùng đã đăng ký sự kiện, hệ thống sẽ thay đổi sang hủy đăng ký
		Chỉ được phép đăng ký cho các sự kiện đơn
		""")
	public ResponseEntity<Void> selfRegisterEvent(@PathVariable String eventId,
		HttpServletRequest httpServletRequest) {
		eventService.selfTriggerRegisterEvent(eventId);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Cá nhân tự check in sự kiện",
		description = """
			Người dùng tự check in cho sự kiện đã đăng ký
			Chức năng hiện tại chỉ phục vụ check in 1 chiều (chỉ check in, không check out)
			Mã xác nhận (code) do ban tổ chức tạo và có hiệu lực trong 10 phút kể từ thời điểm tạo
			""")
	@PostMapping("/{eventId}/self-check-in")
	public ResponseEntity<Void> selfCheckInEvent(@PathVariable String eventId,
		@RequestBody @Valid CheckInRequestDto request,
		HttpServletRequest httpServletRequest) {
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		eventService.selfCheckInEvent(eventId, userId, request.code());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{eventId}/manual-check-in")
	@Operation(
		summary = "Tổ chức check in cho người tham gia sự kiện",
		description = """
			Ban tổ chức check in cho người tham gia sự kiện
			Hiện tại chức năng phục vụ check in 1 chiều (chỉ check in, không check out)
			Danh sách attendeeIds có thể chứa trùng lặp, hệ thống sẽ tự loại bỏ
			""")
	public ResponseEntity<Void> checkInEvent(@PathVariable String eventId,
		@RequestBody @Valid ManualTriggerRequestDto request) {
		Set<String> attendeeIds = new java.util.HashSet<>(
			request.attendeeIds());
		eventService
			.manualCheckInEvent(eventId,
				List.of(attendeeIds.toArray(String[]::new)));
		return ResponseEntity.ok().build();
	}

	@Operation(description = """
		Ban tổ chức đăng ký cho người tham gia sự kiện
		Danh sách attendeeIds có thể chứa trùng lặp, hệ thống sẽ tự loại bỏ
		Nếu người dùng đã đăng ký sự kiện, hệ thống sẽ thay đổi sang hủy đăng ký
		""")
	@PostMapping(EventAPI.ID_MANUAL_TRIGGER_REGISTER)
	public ResponseEntity<Void> registerEvent(@PathVariable String eventId,
		@RequestBody @Valid ManualTriggerRequestDto request) {
		Map<String, Integer> report = new java.util.HashMap<>();
		List<String> attendeeIds = new java.util.ArrayList<>();
		request
			.attendeeIds()
			.forEach(id -> report.put(id, report.getOrDefault(id, 0) + 1));
		report.forEach((id, count) -> {
			if ((count.intValue() & 1) == 1) {
				attendeeIds.add(id);
			}
		});
		eventService.manualTriggerRegisterEvent(eventId, attendeeIds);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Lấy danh sách người tham gia của một sự kiện",
		description = """
			API này cho phép lấy danh sách người tham gia của một sự kiện dựa trên eventId.
			Kết quả trả về được phân trang và sắp xếp theo yêu cầu.
			Người dùng phải có vai trò là HOST organizer có quyền của sự kiện hoặc có vai trò là leader để sử dụng API này.
			""")
	@GetMapping("/{eventId}/attendees")
	public ResponseEntity<PagedModel<EntityModel<AttendeeDto>>> getEventAttendees(
		@PathVariable String eventId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "100") int size,
		@RequestParam(defaultValue = "user.fullName,asc") String sort) {
		Sort sortObj = PageableUtil.parseSort(sort);
		Pageable pageable = PageRequest.of(page, size, sortObj);
		Page<Attendee> attendees = eventAttendeeService
			.getAllAttendeesByEventId(eventId, pageable);
		return ResponseEntity
			.ok(attendeePagedResourcesAssembler
				.toModel(attendees.map(eventAttendeeMapper::toAttendeeDto)));
	}

	@Operation(
		summary = "Lấy danh sách organizer của một sự kiện",
		description = """
			API này cho phép lấy danh sách organizer của một sự kiện dựa trên eventId.
			Người dùng phải là host sự kiện có quyền của sự kiện hoặc có vai trò là leader để sử dụng API này.
			""")
	@GetMapping("/{eventId}/organizers")
	public ResponseEntity<List<EventOrganizerDto>> getEventOrganizers(
		@PathVariable String eventId) {
		List<EventOrganizerDto> list = eventService
			.getEventOrganizers(eventId)
			.stream()
			.map(eventOrganizerMapper::toEventOrganizerDto)
			.toList();
		return ResponseEntity.ok(list);
	}

	@Operation(
		summary = "Cập nhật danh sách organizer của một sự kiện",
		description = """
			API này cho phép cập nhật danh sách organizer của một sự kiện dựa trên eventId.
			Người dùng phải là host sự kiện, admin có quyền của sự kiện để sử dụng API này.
			Trường hợp danh sách organizer rỗng, hệ thống sẽ giữ nguyên danh sách organizer hiện tại.
			Từng thông tin organizer giống như 1 request thêm mới organizer.
			""")
	@PutMapping("/{eventId}/modify-organizers")
	public ResponseEntity<EventDetailResponseDto> updateEventOrganizers(
		@PathVariable String eventId,
		@RequestBody @Valid ListEventOrganizerRequestDto organizerRequests) {
		Event updateEventOrganizers = eventService
			.updateEventOrganizers(eventId, organizerRequests.organizers());
		return ResponseEntity
			.ok()
			.body(eventMapper.toEventDetailResponseDto(updateEventOrganizers));
	}

	@Operation(
		summary = "Khóa người dùng khỏi sự kiện",
		description = """
			API này cho phép khóa (ban) một hoặc nhiều người dùng khỏi sự kiện.
			Người dùng bị khóa sẽ không thể tham gia sự kiện và sẽ bị xóa khỏi danh sách người tham gia nếu họ đã đăng ký.
			Người thực hiện hành động này phải có vai trò là HOST organizer có quyền của sự kiện.
			""")
	@PostMapping("/{eventId}/trigger-ban-user")
	public ResponseEntity<Void> triggerBanUser(@PathVariable String eventId,
		@RequestBody @Valid ManualTriggerRequestDto request,
		HttpServletRequest httpServletRequest) {
		String userId = jwtT.getUserIdFromRequest(httpServletRequest);
		eventService.triggerBan(eventId, request.attendeeIds(), userId);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Cập nhật thông tin cơ bản của một sự kiện",
		description = """
			API này cho phép cập nhật thông tin cơ bản của một sự kiện dựa trên eventId.
			Người dùng phải là host sự kiện, ban tổ chức có quyền MODIFY, admin có quyền của sự kiện để sử dụng API này.
			Các trường trong DTO có thể để trống nếu không muốn cập nhật.
			Sau khi cập nhật, trạng thái của sự kiện sẽ được chuyển về PENDING hoặc ARCHIVED tùy vào dữ liệu truyền vào.
			""")
	@PatchMapping("/{eventId}/update-event")
	public ResponseEntity<EventDetailResponseDto> modifyEvent(
		@PathVariable String eventId,
		@RequestBody @Valid EventUpdateRequestDto dto,
		HttpServletRequest httpServletRequest) {
		EventDetailResponseDto modifiedEvent = eventService
			.updateEvent(eventId, dto,
				jwtT.getUserIdFromRequest(httpServletRequest));
		return ResponseEntity.ok(modifiedEvent);
	}

	@Operation(
		summary = "Cập nhật kết quả thi của thí sinh cho sự kiện cuộc thi",
		description = """
			API này cho phép cập nhật kết quả thi của thí sinh cho sự kiện có loại là CONTEST.
			Người dùng phải là host sự kiện, ban tổ chức có quyền MODIFY, admin có quyền của sự kiện để sử dụng API này.
			Trường hợp thí sinh chưa tham gia sự kiện, hệ thống sẽ báo lỗi và không thực hiện cập nhật.
			""")
	@PutMapping(EventAPI.CONTEST_ID_UPDATE_STANDING)
	public ResponseEntity<Void> updateEventStanding(
		@PathVariable String eventId,
		@RequestBody @Valid ContestExamResultUpdateRequestDto dto,
		HttpServletRequest httpServletRequest) {
		eventService.updateContestStanding(eventId, dto);
		return ResponseEntity.accepted().build();
	}

	@Operation(
		summary = "Thêm đánh giá cho sự kiện hội thảo",
		description = """
			API này cho phép người tham gia thêm đánh giá cho sự kiện có loại là SEMINAR.
			Một người tham gia chỉ được phép đánh giá một lần cho mỗi sự kiện.
			Nội dung đánh giá không được để trống và không vượt quá 500 ký tự.
			Người dùng phải là người tham gia đã check-in thành công cho sự kiện để sử dụng API này.
			""")
	@PostMapping(EventAPI.SEMINAR_ID_ADD_REVIEW)
	public ResponseEntity<Void> addReviewForSeminarEvent(
		@PathVariable String eventId, @RequestBody String reviewContent,
		HttpServletRequest httpServletRequest) {
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		eventService.addReviewForSeminarEvent(eventId, userId, reviewContent);
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Lấy danh sách đánh giá cho sự kiện hội thảo",
		description = """
			API này cho phép lấy danh sách đánh giá cho sự kiện có loại là SEMINAR.
			Danh sách đánh giá chỉ bao gồm nội dung đánh giá, không bao gồm thông tin người đánh giá.
			Người dùng phải là host sự kiện, admin có quyền của sự kiện để sử dụng API này.

			""")
	// TODO: ban tổ chức có quyền VIEW
	@GetMapping(EventAPI.SEMINAR_ID_GET_REVIEWS)
	public ResponseEntity<SeminarReviewsResponseDto> getReviewsForSeminarEvent(
		@PathVariable String eventId, HttpServletRequest httpServletRequest) {
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		List<String> reviews = eventService
			.getReviewsForSeminarEvent(eventId, userId);
		return ResponseEntity.ok(new SeminarReviewsResponseDto(reviews));
	}

	@Operation(
		summary = "Lấy danh sách sự kiện đã đăng ký của người dùng hiện tại",
		description = """
			API này cho phép lấy danh sách các sự kiện mà người dùng hiện tại đã đăng ký tham gia.
			Danh sách có thể được lọc theo trạng thái chức năng của sự kiện và trạng thái tham gia của người dùng.
			Kết quả trả về được phân trang và sắp xếp theo yêu cầu.
			Người dùng phải có vai trò là member trở lên để sử dụng API này.
			""")
	@GetMapping(EventAPI.SEARCH_REGISTERED_EVENTS)
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> getRegisteredEvents(
		@ModelAttribute EventSearchRequestDto request,
		@RequestParam(required = false) FunctionStatus status,
		@RequestParam(required = false) AttendeeStatus attendeeStatus,
		HttpServletRequest httpServletRequest) {
		request.setSortBy(PageableUtil.parseSort(request.getSort()));
		String userId = jwtT.getUserIdFromRequest(httpServletRequest);
		Page<Event> searchUserEvents = eventService
			.searchRegisteredEvents(request, status, userId, attendeeStatus);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(searchUserEvents.map(eventMapper::toEventWrapperDto)));

	}
}

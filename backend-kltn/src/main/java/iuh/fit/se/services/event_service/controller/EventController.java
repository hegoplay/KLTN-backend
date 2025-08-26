package iuh.fit.se.services.event_service.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.AttendeeDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.CheckInRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequestDto;
import iuh.fit.se.services.event_service.dto.request.ManualTriggerRequestDto;
import iuh.fit.se.services.event_service.mapper.EventAttendeeMapper;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.service.EventAttendeeService;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.JwtTokenUtil;
import iuh.fit.se.util.PageableUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(
	name = "Event Management",
	description = """
		API này hỗ trợ các công việc quản lý sự kiện (ngoại trừ sự kiện training và lấy thông tin sự kiện).
		""")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

	EventService eventService;
	EventAttendeeService eventAttendeeService;

	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	PagedResourcesAssembler<AttendeeDto> attendeePagedResourcesAssembler;

	EventMapper eventMapper;
	EventAttendeeMapper eventAttendeeMapper;
	
	JwtTokenUtil jwtT;

	@PostMapping
	public ResponseEntity<EventDetailResponseDto> createEvent(
		@RequestBody @Valid EventCreateRequestDto dto
	) {
		EventDetailResponseDto createdEvent = eventService.createEvent(dto);
		return ResponseEntity.ok(createdEvent);
	}

	@GetMapping("/me/search")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> getEventsByUser(
		@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false,
			defaultValue = "ALL") EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam(required = false) FunctionStatus status
	) {
		EventSearchRequestDto request = new EventSearchRequestDto(keyword,
			eventType, isDone, page, size, PageableUtil.parseSort(sort));

		Page<Event> searchUserEvents = eventService
			.searchMyEvents(request, status);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(searchUserEvents.map(eventMapper::toEventWrapperDto)));
	}

	@GetMapping("/{id}/otp")
	public ResponseEntity<String> getEventOTP(
		@PathVariable String id,
		HttpServletRequest httpServletRequest
	) {
		String otp = eventService.getEventCode(id);
		return ResponseEntity.ok(otp);
	}

	@PostMapping("/self-trigger-register")
	public ResponseEntity<Void> selfRegisterEvent(
		@RequestParam String eventId,
		HttpServletRequest httpServletRequest
	) {
		eventService.selfTriggerRegisterEvent(eventId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{eventId}/self-check-in")
	public ResponseEntity<Void> selfCheckInEvent(
		@PathVariable String eventId,
		@RequestBody @Valid CheckInRequestDto request,
		HttpServletRequest httpServletRequest
	) {
		String tokenFromRequest = jwtT.getTokenFromRequest(httpServletRequest);
		String userId = jwtT.getUserIdFromToken(tokenFromRequest);
		eventService.selfCheckInEvent(eventId, userId, request.code());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{eventId}/manual-check-in")
	public ResponseEntity<Void> checkInEvent(
		@PathVariable String eventId,
		@RequestBody @Valid ManualTriggerRequestDto request
	) {
		Set<String> attendeeIds = new java.util.HashSet<>(request.attendeeIds());
		eventService.manualCheckInEvent(eventId, List.of(attendeeIds.toArray(String[]::new)));
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{eventId}/manual-register")
	public ResponseEntity<Void> registerEvent(
		@PathVariable String eventId,
		@RequestBody @Valid ManualTriggerRequestDto request
	) {
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

	@GetMapping("/{id}/attendees")
	public ResponseEntity<PagedModel<EntityModel<AttendeeDto>>> getEventAttendees(
		@PathVariable String id,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "100") int size,
		@RequestParam(defaultValue = "user.fullName,asc") String sort
	) {
		Sort sortObj = PageableUtil.parseSort(sort);
		Pageable pageable = PageRequest.of(page, size, sortObj);
		Page<Attendee> attendees = eventAttendeeService
			.getAllAttendeesByEventId(id, pageable);
		return ResponseEntity
			.ok(attendeePagedResourcesAssembler
				.toModel(attendees.map(eventAttendeeMapper::toAttendeeDto)));
	}

}

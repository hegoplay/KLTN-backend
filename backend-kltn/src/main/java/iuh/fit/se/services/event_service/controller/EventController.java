package iuh.fit.se.services.event_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequest;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.ContextUtil;
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
		"""
)
@SecurityRequirement(name = "bearerAuth")
public class EventController {

	EventService eventService;
	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	EventMapper eventMapper;

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
			defaultValue = "ALL"
		) EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam(required = false) FunctionStatus status
	) {
		EventSearchRequest request = new EventSearchRequest(keyword, eventType,
			isDone, page, size, PageableUtil.parseSort(sort));

		Page<Event> searchUserEvents = eventService
			.searchMyEvents(request, status);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(searchUserEvents.map(eventMapper::toEventWrapperDto)));
	}
}

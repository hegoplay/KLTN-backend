package iuh.fit.se.services.event_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Event;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.EventWrapperDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;
import iuh.fit.se.services.event_service.dto.request.EventSearchRequest;
import iuh.fit.se.services.event_service.dto.request.EventStatusUpdateRequest;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.service.EventService;
import iuh.fit.se.util.PageableUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(
	name = "Event Public Management",
	description = """
		API này hỗ trợ các công việc liên quan đến sự kiện mà không cần xác thực.
		"""
)
public class EventPublicController {
	
	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	EventService eventService;
	EventMapper eventMapper;
	
	@GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") EventSearchType eventType,
            @RequestParam(required = false) Boolean isDone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "location.startTime,asc") String sort) {
        EventSearchRequest request = new EventSearchRequest(
            keyword, eventType, isDone, page, size, PageableUtil.parseSort(sort)
        );

        Page<Event> events = eventService.searchPublicEvents(request);
        return ResponseEntity.ok(pagedResourcesAssembler
			.toModel(events.map(eventMapper::toEventWrapperDto)));
    }

	@GetMapping("/{eventId}")
	public ResponseEntity<EventDetailResponseDto> getEventById(
		@RequestParam String eventId
	) {
		log.info("Getting event by id: {}", eventId);
		EventDetailResponseDto event = eventService.getEventById(eventId);
		return ResponseEntity.ok(event);
	}
	
	
	
}

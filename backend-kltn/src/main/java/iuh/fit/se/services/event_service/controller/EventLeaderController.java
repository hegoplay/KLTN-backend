package iuh.fit.se.services.event_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
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
	name = "Event Admin Management",
	description = """
		API này hỗ trợ các công việc quản lý sự kiện của leader.
		"""
)
@SecurityRequirement(name = "bearerAuth")
public class EventLeaderController {

//	duyệt sự kiện
	EventService eventService;
	PagedResourcesAssembler<EventWrapperDto> pagedResourcesAssembler;
	EventMapper eventMapper;
	
	@GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> searchEvents(
    	@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false,
			defaultValue = "ALL"
		) EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam FunctionStatus status) {
        EventSearchRequestDto request = new EventSearchRequestDto(
            keyword, eventType, isDone, page, size, PageableUtil.parseSort(sort)
        );

        Page<Event> events = eventService.searchAllEvents(request,status);
        return ResponseEntity.ok(pagedResourcesAssembler
			.toModel(events.map(eventMapper::toEventWrapperDto)));
    }
	
	@GetMapping("/user/{userId}/search")
	public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> getEventsByUser(
		@PathVariable String userId,
		@RequestParam(required = false) String keyword,
		@RequestParam(
			required = false,
			defaultValue = "ALL"
		) EventSearchType eventType,
		@RequestParam(required = false) Boolean isDone,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "location.startTime,asc") String sort,
		@RequestParam FunctionStatus status) {
		EventSearchRequestDto request = new EventSearchRequestDto(
			keyword, eventType, isDone, page, size, PageableUtil.parseSort(sort)
		);

		Page<Event> events = eventService.searchUserEvents(request, null,status);
		return ResponseEntity.ok(pagedResourcesAssembler
			.toModel(events.map(eventMapper::toEventWrapperDto)));
	}
	
	@DeleteMapping("/{eventId}")
	public ResponseEntity<Void> deleteEvent(
		@PathVariable String eventId
	) {
		eventService.deleteEvent(eventId);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{eventId}/trigger-done")
	public ResponseEntity<Void> triggerEventDone(
		@PathVariable String eventId
	) {
		eventService.triggerEventDone(eventId); 
		return ResponseEntity.accepted().build();
	}
	
	@PatchMapping("/{eventId}/status")
	public ResponseEntity<Void> updateEventStatus(
		@RequestParam String eventId,
		@RequestBody EventStatusUpdateRequestDto request
	) {
		eventService.updateEventStatus(eventId,request.status());
		return ResponseEntity.accepted().build();
	}
	
}

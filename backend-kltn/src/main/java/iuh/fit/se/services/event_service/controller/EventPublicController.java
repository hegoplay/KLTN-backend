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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Event;
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
	JwtTokenUtil jwtTokenUtil;
	
	@GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<EventWrapperDto>>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") EventSearchType eventType,
            @RequestParam(required = false) Boolean isDone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "location.startTime,asc") String sort) {
        EventSearchRequestDto request = new EventSearchRequestDto(
            keyword, eventType, isDone, page, size, PageableUtil.parseSort(sort)
        );

        Page<Event> events = eventService.searchPublicEvents(request);
        return ResponseEntity.ok(pagedResourcesAssembler
			.toModel(events.map(eventMapper::toEventWrapperDto)));
    }

	@GetMapping("/{eventId}")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<EventDetailResponseDto> getEventById(
		@PathVariable String eventId,
		HttpServletRequest request
	) {
//		java.util.Enumeration<String> headerNames = request.getHeaderNames();
//	    while (headerNames.hasMoreElements()) {
//	        String headerName = headerNames.nextElement();
//	        log.info("Header: {} = {}", headerName, request.getHeader(headerName));
//	    }
		String tokenFromRequest = jwtTokenUtil.getTokenFromRequest(request);
		if (tokenFromRequest==null) {
			EventDetailResponseDto event = eventService.getEventById(eventId);
			return ResponseEntity.ok(event);
			
		}
		log.info("Token from request: {}", tokenFromRequest);
		String userId = jwtTokenUtil.getUserIdFromToken(tokenFromRequest);
		EventDetailResponseDto event = eventService.getEventByIdAndUserId(eventId, userId);
		
		return ResponseEntity.ok(event);
	}
	
	
	
}

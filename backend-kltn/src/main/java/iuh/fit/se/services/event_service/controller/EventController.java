package iuh.fit.se.services.event_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.services.event_service.dto.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.service.EventService;
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
@Tag(name = "Event Management", description = """
	API này hỗ trợ các công việc quản lý sự kiện (ngoại trừ sự kiện training và lấy thông tin sự kiện).
	""")
@SecurityRequirement(name = "bearerAuth")
public class EventController {
	
	EventService eventService;
	
	@PostMapping
	public ResponseEntity<EventDetailResponseDto> createEvent(
		@RequestBody @Valid EventCreateRequestDto dto
		) {
		EventDetailResponseDto createdEvent = eventService.createEvent(dto);
		return ResponseEntity.ok(createdEvent);
	}
}

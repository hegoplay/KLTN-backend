package iuh.fit.se.event_service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.LocationDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.service.EventService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class EventIntegrationTest {

	@Autowired
	EventService eventService;

	@Test @WithMockUser(username = "admin", roles = {"ADMIN"})
	public void addEvent() {
		LocationDto locationDto = LocationDto
			.builder()
			.destination("xxx")
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusHours(2))
			.build();
		EventCreateRequestDto dto = new EventCreateRequestDto("Test Seminar",
			"Description", locationDto, Integer.valueOf(1),
			FunctionStatus.PENDING, List.of(), null,
			EventCategory.SEMINAR);

		EventDetailResponseDto event = eventService.createEvent(dto);
		log.info("Created event: {}", event);
	}

}

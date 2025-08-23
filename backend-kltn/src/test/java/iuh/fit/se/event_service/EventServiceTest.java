package iuh.fit.se.event_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Location;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.LocationDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.EventCreateRequestDto;
import iuh.fit.se.services.event_service.mapper.EventMapper;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.event_service.serviceImpl.EventServiceImpl;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class EventServiceTest {
	@Mock
	private EventMapper eventMapper;

	@Mock
	private UserService userService;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventServiceImpl eventService;

	private EventCreateRequestDto seminarRequest;
	private EventCreateRequestDto contestRequest;
	private EventCreateRequestDto trainingRequest;

	private LocationDto locationDto;

	private Location location;

	@BeforeEach
	void setUp() {
		// Setup test data
		// EventOrganizerDto organizer = new EventOrganizerDto("user123",,"Main
		// Organizer", List.of());

		locationDto = LocationDto
			.builder()
			.destination("xxx")
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusHours(2))
			.build();

		location = Location
			.builder()
			.destination("xxx")
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusHours(2))
			.build();

		seminarRequest = new EventCreateRequestDto("Test Seminar",
			"Description", locationDto, Integer.valueOf(1),
			FunctionStatus.PENDING, List.of(), null, EventCategory.SEMINAR);

		contestRequest = new EventCreateRequestDto("Test Contest",
			"Description", locationDto, Integer.valueOf(1),
			FunctionStatus.PENDING, List.of(), null, EventCategory.CONTEST);

		trainingRequest = new EventCreateRequestDto("Test Training",
			"Description", locationDto, Integer.valueOf(1),
			FunctionStatus.PENDING, List.of(), null, EventCategory.CONTEST);
	}

	@Test @WithMockUser(username = "hegoplay", roles = {"MEMBER"})
	void createEvent_WithSeminarCategory_ShouldReturnSeminar() {
		// Arrange
		Seminar mockSeminar = new Seminar();
		mockSeminar.setId("seminar-123");
		mockSeminar.setTitle("Test Seminar");

		EventDetailResponseDto expectedResponse = EventDetailResponseDto
			.builder()
			.id("seminar-123")
			.title("Test Seminar")
			.category(EventCategory.SEMINAR)
			.status(FunctionStatus.PENDING)
			.build();

		when(eventRepository.save(any(Event.class))).thenReturn(mockSeminar);
		when(eventMapper.toEventDetailResponseDto(any(Seminar.class)))
			.thenReturn(expectedResponse);
		when(eventMapper.toSeminar(seminarRequest)).thenReturn(mockSeminar);
		when(userService.getCurrentUser()).thenReturn(User.builder().build()); // Mock
																				// user
																				// retrieval
																				// as
																				// needed

		// Mock factory behavior (you might need to spy or mock the factory)
		// For simplicity, we assume the factory works correctly and focus on
		// service logic

		// Act
		EventDetailResponseDto result = eventService
			.createEvent(seminarRequest);
		log.info("Result: {}", result);
		// Assert
		assertNotNull(result);
		assertEquals(expectedResponse.getCategory(), result.getCategory());
		verify(eventRepository, times(1)).save(any(Event.class));
	}
}

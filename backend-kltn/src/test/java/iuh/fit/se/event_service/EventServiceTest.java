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

import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Location;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.request.TrainingEventCreateRequestDto;
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

	private SingleEventCreateRequestDto seminarRequest;
	private BaseEventCreateRequestDto contestRequest;
	private BaseEventCreateRequestDto trainingRequest;

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

		seminarRequest = SingleEventCreateRequestDto
			.builder()
			.title("Test Seminar")
			.description("Description")
			.location(locationDto)
			.multiple(Integer.valueOf(1))
			.status(FunctionStatus.PENDING)
			.organizers(List.of())
			.category(EventCategory.SEMINAR)
			.build();

		contestRequest = SingleEventCreateRequestDto
			.builder()
			.title("Test Contest")
			.description("Description")
			.location(locationDto)
			.multiple(Integer.valueOf(1))
			.status(FunctionStatus.PENDING)
			.organizers(List.of())
			.category(EventCategory.CONTEST)
			.build();

		
		
//		trainingRequest = new BaseEventCreateRequestDto("Test Training",
//			"Description", locationDto, Integer.valueOf(1),
//			FunctionStatus.PENDING, List.of(), EventCategory.CONTEST);
		trainingRequest = TrainingEventCreateRequestDto
			.builder()
			.title("Test Training")
			.description("Description")
			.location(locationDto)
			.multiple(Integer.valueOf(1))
			.organizers(List.of())
//			.category(EventCategory.TRAINING_EVENT)
			.build();
	}

	@Test
	@WithMockUser(username = "hegoplay", roles = {"MEMBER"})
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
		when(eventMapper.toSeminarIgnoreOrganizer(seminarRequest)).thenReturn(mockSeminar);
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

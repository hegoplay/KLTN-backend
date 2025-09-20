package iuh.fit.se.event_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import iuh.fit.se.api.EventAPI;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Seminar;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.ContestExamResultUpdateRequestDto;
import iuh.fit.se.services.event_service.dto.request.ExamResultRequestDto;
import iuh.fit.se.services.event_service.dto.request.ManualTriggerRequestDto;
import iuh.fit.se.services.event_service.dto.request.SingleEventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.response.SeminarReviewsResponseDto;
import iuh.fit.se.services.event_service.repository.EventAttendeeRepository;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.user_service.dto.LoginResponseDto;
import iuh.fit.se.services.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
	properties = "spring.profiles.active=test",
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Sql(
	scripts = "classpath:test-schema.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ContestAttendeeTest {

	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EventAttendeeRepository eventAttendeeRepository;

	private RestClient restClient;

	@LocalServerPort // ← Spring sẽ inject random port vào đây
	private int serverPort;

	public String jwtToken;

	private List<EventDetailResponseDto> eventCreateResponseDtos = new ArrayList<>();

	@BeforeEach
	void setUp() {
		RestClient tempClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + "/api")
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.build();
		LoginResponseDto loginResponse = tempClient
			.post()
			.uri("/auth/login")
			.body(Map.of("username", "hegoplay", "password", "Manhvip399!"))
			.retrieve()
			.body(LoginResponseDto.class);

		jwtToken = loginResponse.getAccessToken();
		restClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + EventAPI.BASE_URL)
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
			.build();

		SingleEventCreateRequestDto requestDto = SingleEventCreateRequestDto
			.builder()
			.title("Sự kiện 1")
			.description("Mô tả sự kiện 1")
			.location(
				new LocationDto("diem dich", LocalDateTime.now().plusMinutes(3),
					LocalDateTime.now().plusHours(3)))
			.multiple(1)
			.status(FunctionStatus.PENDING)
			.organizers(List.of())
			.category(EventCategory.CONTEST)
			.build();

		eventCreateResponseDtos.add(addSingleEvent(requestDto, restClient));
		requestDto = SingleEventCreateRequestDto
			.builder()
			.title("Sự kiện 2")
			.description("Mô tả sự kiện 2")
			.location(
				new LocationDto("diem dich", LocalDateTime.now().plusMinutes(3),
					LocalDateTime.now().plusHours(3)))
			.multiple(1)
			.status(FunctionStatus.PENDING)
			.organizers(List.of())
			.category(EventCategory.SEMINAR)
			.build();
		eventCreateResponseDtos.add(addSingleEvent(requestDto, restClient));

	}

	private EventDetailResponseDto addSingleEvent(
		SingleEventCreateRequestDto requestDto, RestClient client) {
		return restClient
			.post()
			.body(requestDto)
			.retrieve()
			.body(EventDetailResponseDto.class);
	}

	@AfterEach
	void tearDown() {
		for (EventDetailResponseDto dto : eventCreateResponseDtos) {
			eventRepository.deleteById(dto.getId());
		}
	}

	@Test
	void testAddExamResult() {
		Event event = eventRepository
			.findById(eventCreateResponseDtos.get(0).getId())
			.orElseThrow(() -> new IllegalArgumentException("Event not found"));
		event.setStatus(FunctionStatus.ACCEPTED);
		eventRepository.save(event);
		User user = userRepository.findByUsername("user1").orElse(new User());
		ManualTriggerRequestDto dto = new ManualTriggerRequestDto(
			List.of(user.getId()));
		restClient
			.post()
			.uri("/{eventId}/manual-trigger-register",
				eventCreateResponseDtos.get(0).getId())
			.body(dto)
			.retrieve()
			.toBodilessEntity();

		ContestExamResultUpdateRequestDto examResultDto = new ContestExamResultUpdateRequestDto(
			List.of(new ExamResultRequestDto(user.getId(), 1, 5)));

		EventDetailResponseDto edrDto = restClient
			.put()
			.uri(EventAPI.CONTEST_ID_UPDATE_STANDING,
				eventCreateResponseDtos.get(0).getId())
			.body(examResultDto)
			.retrieve()
			.body(EventDetailResponseDto.class);

		user = userRepository.findById("user2").orElse(new User());
		examResultDto = new ContestExamResultUpdateRequestDto(
			List.of(new ExamResultRequestDto(user.getId(), 1, 5)));

		Exception assertThrows2 = assertThrows(Exception.class, () -> {
			User nUser = userRepository.findById("user2").orElse(new User());
			restClient
				.put()
				.uri(EventAPI.CONTEST_ID_UPDATE_STANDING,
					eventCreateResponseDtos.get(0).getId())
				.body(new ContestExamResultUpdateRequestDto(
					List.of(new ExamResultRequestDto(nUser.getId(), 1, 5))))
				.retrieve()
				.body(EventDetailResponseDto.class);
		});
		log.info("Exception: {}", assertThrows2.getMessage());

	}

	@Test
	void testGetReview() {
		Event event = eventRepository
			.findById(eventCreateResponseDtos.get(1).getId())
			.orElseThrow(() -> new IllegalArgumentException("Event not found"));

		event.setStatus(FunctionStatus.ACCEPTED);
		
		log.info("testGetReview - Event host: {}", event.getHost().getUsername());

		User user = userRepository.findByUsername("user1").orElse(new User());

		Attendee attendee = Attendee
			.builder()
			.user(user)
			.userId(user.getId())
			.event(event)
			.eventId(event.getId())
			.status(AttendeeStatus.CHECKED)
			.build();
		eventAttendeeRepository.save(attendee);

		RestClient tempClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + "/api")
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.build();
		LoginResponseDto loginResponse = tempClient
			.post()
			.uri("/auth/login")
			.body(Map.of("username", "user1", "password", "password"))
			.retrieve()
			.body(LoginResponseDto.class);

		String jwt = loginResponse.getAccessToken();

		tempClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + EventAPI.BASE_URL)
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
			.build();

		tempClient
			.post()
			.uri(EventAPI.SEMINAR_ID_ADD_REVIEW, event.getId())
			.body("Rất hay")
			.retrieve()
			.toBodilessEntity();
		tempClient
			.post()
			.uri(EventAPI.SEMINAR_ID_ADD_REVIEW, event.getId())
			.body("Rất hay 2")
			.retrieve()
			.toBodilessEntity();

		SeminarReviewsResponseDto dto = restClient
			.get()
			.uri(EventAPI.SEMINAR_ID_GET_REVIEWS, event.getId())
			.retrieve()
			.body(SeminarReviewsResponseDto.class);
		assertEquals(2, dto.getReviews().size());
	}
}

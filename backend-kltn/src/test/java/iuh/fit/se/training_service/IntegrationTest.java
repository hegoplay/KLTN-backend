package iuh.fit.se.training_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.event_service.dto.request.BaseEventCreateRequestDto;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.training_service.dto.TrainingCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingDetailDto;
import iuh.fit.se.services.training_service.dto.TrainingEventListCreateRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingMemberRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingStatusRequestDto;
import iuh.fit.se.services.training_service.dto.TrainingWrapperDto;
import iuh.fit.se.services.training_service.repository.TrainingRepository;
import iuh.fit.se.services.training_service.service.TrainingService;
import iuh.fit.se.services.user_service.dto.LoginResponseDto;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
	properties = "spring.profiles.active=test",
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Sql(
	scripts = "classpath:test-schema.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class IntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	TrainingService trainingService;

	@Autowired
	TrainingRepository trainingRepository;
	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserService userService;

	private RestClient restClient;

	@LocalServerPort // ← Spring sẽ inject random port vào đây
	private int serverPort;

	public String jwtToken;

	private TrainingCreateRequestDto trainingDto;
	private TrainingDetailDto trainingResponseDto;
	private TrainingCreateRequestDto subTrainingDto;
	private TrainingDetailDto subTrainingResponseDto;

	@BeforeEach
	void init() {
		// Đầu tiên, tạo RestClient tạm để login (chưa có token)
		RestClient tempClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + "/api")
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.build();

		// Login để lấy token
		LoginResponseDto loginResponse = tempClient
			.post()
			.uri("/auth/login")
			.body(Map.of("username", "admin", "password", "admin"))
			.retrieve()
			.body(LoginResponseDto.class);

		jwtToken = loginResponse.getAccessToken();

		// Tạo RestClient chính thức với token
		restClient = RestClient
			.builder()
			.baseUrl("http://localhost:" + serverPort + "/api")
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
			.build();
		trainingDto = TrainingCreateRequestDto
			.builder()
			.title("Test Training")
			.location(LocationDto
				.builder()
				.destination("123 Test St")
				.startTime(LocalDateTime.now().plusHours(2))
				.endTime(LocalDateTime.now().plusDays(4))
				.build())
			.description("This is a test training")
			.mentorIds(new HashSet<String>())
			.status(FunctionStatus.PENDING)
			.trainingEvents(List
				.of(BaseEventCreateRequestDto
					.builder()
					.title("Test Accepted Event")
					.description("This is a accepted event")
					.multiple(1)
					.location(LocationDto
						.builder()
						.destination("123 Event St")
						.startTime(LocalDateTime.now().plusDays(1))
						.endTime(LocalDateTime.now().plusDays(3))
						.build())
					.organizers(new ArrayList<>())
					.build()))
			.build();

		trainingResponseDto = restClient
			.post()
			.uri("/trainings")
			.body(trainingDto)
			.retrieve()
			.body(TrainingDetailDto.class);

		var testTrainingEvent = restClient
			.get()
			.uri("/events/{eventId}",
				Map
					.of("eventId",
						trainingResponseDto.getTrainingEvents().get(0).getId()))
			.retrieve()
			.body(EventDetailResponseDto.class);
		log.info("training event: {}", testTrainingEvent);
		log.info("Created training: {}", trainingResponseDto);
		// chỉnh sang ACCEPTED cho training này
		restClient
			.patch()
			.uri("/leader/trainings/{trainingId}/status",
				Map.of("trainingId", trainingResponseDto.getId()))
			.body(new TrainingStatusRequestDto(FunctionStatus.ACCEPTED))
			.retrieve()
			.toBodilessEntity();
		// push thêm event vào training

		BaseEventCreateRequestDto dto1 = BaseEventCreateRequestDto
			.builder()
			.title("Test Event")
			.description("This is a test event")
			.multiple(1)
			.location(LocationDto
				.builder()
				.destination("123 Event St")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(2))
				.build())
			.organizers(new ArrayList<>())
			.build();
		BaseEventCreateRequestDto dto2 = BaseEventCreateRequestDto
			.builder()
			.title("Test Event 2")
			.description("This is a test event 2")
			.location(LocationDto
				.builder()
				.destination("456 Event St")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(2))
				.build())
			.multiple(1)
			.organizers(new ArrayList<>())
			.build();
		trainingResponseDto = restClient
			.post()
			.uri("/trainings/{trainingId}/events",
				Map.of("trainingId", trainingResponseDto.getId()))
			.body(new TrainingEventListCreateRequestDto(List.of(dto1, dto2)))
			.retrieve()
			.body(TrainingDetailDto.class);

		subTrainingDto = TrainingCreateRequestDto
			.builder()
			.title("sub Test Training")
			.location(LocationDto
				.builder()
				.destination("123 Test St")
				.startTime(LocalDateTime.now().plusHours(2))
				.endTime(LocalDateTime.now().plusDays(4))
				.build())
			.description("This is a test training")
			.mentorIds(new HashSet<String>())
			.status(FunctionStatus.PENDING)
			.trainingEvents(List.of())
			.build();

		subTrainingResponseDto = restClient
			.post()
			.uri("/trainings")
			.body(subTrainingDto)
			.retrieve()
			.body(TrainingDetailDto.class);
	}

	@AfterEach
	void cleanup() {
		// Xoá dữ liệu test nếu cần thiết
		if (trainingResponseDto != null) {
			trainingRepository.deleteById(trainingResponseDto.getId());
			trainingResponseDto.getTrainingEvents().forEach(event -> {
				eventRepository.deleteById(event.getId());
			});
		}
		if (subTrainingResponseDto != null) {
			trainingRepository.deleteById(subTrainingResponseDto.getId());
			subTrainingResponseDto.getTrainingEvents().forEach(event -> {
				eventRepository.deleteById(event.getId());
			});
		}

	}

	@ParameterizedTest
	@MethodSource("trainingEndpointProvider")
	void getTrainingDetail_shouldReturnCorrectNumberOfEvents(
		String endpoint,
		int expectedEventCount,
		String description
	) {

		// Kết quả trả về sau khi thêm sự kiện vào khóa học
		TrainingDetailDto responseDto = restClient
			.get()
			.uri(endpoint, Map.of("trainingId", trainingResponseDto.getId()))
			.retrieve()
			.body(TrainingDetailDto.class);

		log.info("{}: {}", description, responseDto);

		assertEquals(expectedEventCount,
			responseDto.getTrainingEvents().size());
	}

	private static Stream<Arguments> trainingEndpointProvider() {
		return Stream
			.of(Arguments
				.of("/public/trainings/{trainingId}", 1, "Public training"),
				Arguments
					.of("/leader/trainings/{trainingId}", 3,
						"Leader training"));
	}

	@Test
	void add_event_to_training_out_of_time_bound_test() {
		BaseEventCreateRequestDto dto1 = BaseEventCreateRequestDto
			.builder()
			.title("Test Event")
			.description("This is a test event")
			.multiple(1)
			.location(LocationDto
				.builder()
				.destination("123 Event St")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(5))
				.build())
			.organizers(new ArrayList<>())
			.build();
		HttpClientErrorException exception = assertThrows(
			HttpClientErrorException.class, () -> {
				restClient
					.post()
					.uri("/trainings/{trainingId}/events",
						trainingResponseDto.getId())
					.body(new TrainingEventListCreateRequestDto(List.of(dto1)))
					.retrieve()
					.toBodilessEntity();
			});

		// Verify status code và error message
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@ParameterizedTest
	@MethodSource("trainingSearchEndpointProvider")
	void search_all_training_test(
		String endpoint,
		int expectedEventCount,
		String description
	) {
		PagedModel<EntityModel<TrainingWrapperDto>> res = restClient
			.get()
			.uri(endpoint)
			.retrieve()
			.body(PagedModel.class);

		//
		log.info("search_all_training_test | {}: {}", description, res);

		// Verify status code và error message
		assertEquals(expectedEventCount, res.getMetadata().getTotalElements());
	}

	private static Stream<Arguments> trainingSearchEndpointProvider() {
		return Stream
			.of(Arguments
				.of("/public/trainings/search", 1,
					"Public training search list "),
				Arguments
					.of("/leader/trainings/search", 2,
						"Leader training search list"),
				Arguments
					.of("/trainings/me/search", 2,
						"Get my training search list"));
	}

	@Test
	void add_members_test() {
		var user = userService.getUserByKeyword("user1");

		var user2 = userService.getUserByKeyword("admin");

		TrainingMemberRequestDto dto = new TrainingMemberRequestDto(
			List.of(user.getId(), user2.getId()), null);

		restClient
			.put()
			.uri("/trainings/{trainingId}/members",
				Map.of("trainingId", trainingResponseDto.getId()))
			.body(dto)
			.retrieve()
			.toBodilessEntity();

		TrainingDetailDto tempTrainingDetailDto = restClient
			.get()
			.uri("/trainings/{trainingId}",
				Map.of("trainingId", trainingResponseDto.getId()))
			.retrieve()
			.body(TrainingDetailDto.class);

		assertEquals(2, tempTrainingDetailDto.getMentors().size());
	}
}

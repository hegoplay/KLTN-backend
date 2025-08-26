package iuh.fit.se.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.GlobalConfiguration;
import iuh.fit.se.entity.Location;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.repository.GlobalConfigurationRepository;
import iuh.fit.se.services.event_service.repository.EventRepository;
import iuh.fit.se.services.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class InitConfig {
	PasswordEncoder passwordEncoder;

	@Bean
	@Transactional
	CommandLineRunner initDemoUser(
		UserRepository userRepository,
		EventRepository eventRepository,
		GlobalConfigurationRepository globalConfigurationRepo
	) {
		return args -> {
			// Kiểm tra xem user demo đã tồn tại chưa
			if (userRepository.findByUsername("admin").isEmpty()) {
				User demoUser = User
					.builder()
					.username("admin")
					.password(passwordEncoder.encode("admin"))
					.email("demo@example.com")
					.fullName("Demo User")
					.role(UserRole.ADMIN)
					.dateOfBirth(LocalDate.of(2000, 1, 1))
					.disabled(false)
					.build();

				// cấu hình contest với có người tham gia là chính mình
				try {
					Event event = Contest
						.builder()
						.title("Sự kiện mẫu")
						.content("Đây là sự kiện mẫu được tạo tự động.")
						.location(new Location("H32",
							LocalDateTime.now().plusDays(-3),
							LocalDateTime.now().plusDays(3).plusHours(2)))
						.multiple(1)
						.host(demoUser)
						.status(
							iuh.fit.se.entity.enumerator.FunctionStatus.ACCEPTED)
						.done(false)
						.ableToRegister(true)
						.build();

					event
						.addAttendee(Attendee
							.builder()
							.user(demoUser)
							.status(AttendeeStatus.REGISTERED)
							.build());
					log.info("Event info: {}", event);
					eventRepository.save(event);
					log.info("Đã tạo tài khoản demo: admin/admin");
					log.info("Đã tạo sự kiện mẫu.");
				} catch (Exception e) {
					log.info("Lỗi khi tạo sự kiện mẫu: " + e.getMessage());
					// e.printStackTrace();
					// userRepository.delete(demoUser);
					throw e;
				}
			}

			createSampleUser(User
				.builder()
				.username("hegoplay")
				.password(passwordEncoder.encode("Manhvip399!"))
				.email("pmanh47@gmail.com")
				.fullName("Pham Mạnh")
				.role(UserRole.MEMBER)
				.dateOfBirth(LocalDate.of(2000, 1, 1))
				.disabled(false)
				.build(), userRepository);

			IntStream.range(1, 11).forEach(i -> {
				User user = User
					.builder()
					.username("user" + i)
					.password(passwordEncoder.encode("password"))
					.email("user" + i + "@example.com")
					.fullName("User " + i)
					.role(UserRole.NONE)
					.dateOfBirth(LocalDate.of(2000, 1, 1))
					.disabled(false)
					.build();
				createSampleUser(user, userRepository);
			});

			if (globalConfigurationRepo
				.findByConfigKey(GlobalConfiguration.KEY_LAST_RESET_POINT_TIME)
				.isEmpty()) {
				GlobalConfiguration config = new GlobalConfiguration();
				config
					.setConfigKey(
						GlobalConfiguration.KEY_LAST_RESET_POINT_TIME);
				config
					.setConfigValueFromDateTime(
						LocalDateTime.now().minusYears(1));
				globalConfigurationRepo.save(config);
			}
		};
	}

	private void createSampleUser(User user, UserRepository userRepo) {
		if (userRepo.findByUsername(user.getUsername()).isEmpty()) {
			userRepo.save(user);
			log.info("Đã tạo tài khoản: {}/{}", user.getUsername(), "password");
		}
	}
}

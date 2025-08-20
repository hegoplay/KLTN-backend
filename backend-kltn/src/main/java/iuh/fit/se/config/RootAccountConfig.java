package iuh.fit.se.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.services.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class RootAccountConfig {
	PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initDemoUser(UserRepository userRepository) {
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

				userRepository.save(demoUser);
				System.out.println("Đã tạo tài khoản demo: admin/admin");
			}
		};
	}
}

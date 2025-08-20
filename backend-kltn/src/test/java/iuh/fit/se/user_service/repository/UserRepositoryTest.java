package iuh.fit.se.user_service.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.service.UserService;


@DataJpaTest
public class UserRepositoryTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
    TestEntityManager entityManager;
	
	@Test
	void insertUserTest() {
		User user = User.builder()
			.username("testuser")
			.password("testpassword")
			.fullName("Test User")
			.email("test@gmail.com")
			.build();
		entityManager.persist(user);
        entityManager.flush();
        
		User saveUser = userService.saveUser(user);
		// Kiểm tra xem user đã được lưu thành công hay chưa
		assertTrue(Objects.nonNull(saveUser) , "User should be saved successfully");
	}
}

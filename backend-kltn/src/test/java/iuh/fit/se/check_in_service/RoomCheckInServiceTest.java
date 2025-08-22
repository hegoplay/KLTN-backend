package iuh.fit.se.check_in_service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.room_check_in_service.service.RoomCheckInService;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class RoomCheckInServiceTest {

	@Autowired
	private RoomCheckInService checkInService;
	
	@Autowired
	private UserService	 userService;
	
//	@Test
//	public void testToggleCheckIn() {
//		User user = userService.getUserByUsernameOrEmail("admin");
//		String userId = user.getUserId();
//		checkInService.checkIn(userId);
//		System.out.println("User " + userId + " is checked in: ");
//	}
	@RepeatedTest(4)
	public void testToggle() {
		User user = userService.getUserByUsernameOrEmail("admin");
		String userId = user.getId();
		boolean initStatus = checkInService.isCheckedIn(userId);
		log.info("Initial check-in: {}", initStatus);
		boolean isCheckedIn = checkInService.toggleCheckIn(userId);
		boolean beforeStatus = checkInService.isCheckedIn(userId);
		log.info("Middle checked in: {}", beforeStatus);
		assertTrue(!initStatus == beforeStatus, "Toggle check-in status failed");
		System.out.println("User " + userId + " is checked in: " + isCheckedIn);
		checkInService.toggleCheckIn(userId);
		boolean afterStatus = checkInService.isCheckedIn(userId);
		log.info("After checked in: {}", afterStatus);
		assertTrue(afterStatus == initStatus, "Toggle check-out status failed");
	}
}

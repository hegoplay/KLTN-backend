package iuh.fit.se.services.user_service.service;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;

public interface UserService {

	User saveUser(User user);

	void registerUser(RegisterRequestDto dto);
	
	User loadUserByUsernameOrEmail(String usernameOrEmail);
	
	User getUserById(String userId);
	
	User getUserByUsernameOrEmail(String usernameOrEmail);
	
	User getCurrentUser();
	
	User getUserByKeyword(String keyword);

	void resetAllAttendancePoint();
	
	void resetAllContributionPoint();
}

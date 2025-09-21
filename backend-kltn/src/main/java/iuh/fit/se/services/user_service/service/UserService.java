package iuh.fit.se.services.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;
import iuh.fit.se.services.user_service.dto.UpdatePasswordRequestDto;
import iuh.fit.se.services.user_service.dto.UserUpdateInfoRequestDto;

public interface UserService {

	User saveUser(User user);

	void registerUser(RegisterRequestDto dto);
	
	User loadUserByUsernameOrEmail(String usernameOrEmail);
	User getUserById(String userId);
	User getUserByUsernameOrEmail(String usernameOrEmail);
	User getCurrentUser();
	User getUserByKeyword(String keyword);
	User updateUserInfo(String userId, UserUpdateInfoRequestDto dto);

	void updateMyPassword(String userId, UpdatePasswordRequestDto dto);
	void updateUserPassword(User user, String newPassword);
	
	void resetAllAttendancePoint();
	void resetAllContributionPoint();
	
	Page<User> searchUsers(String keyword, Pageable pageable);
}

package iuh.fit.se.services.user_service.serviceImpl;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	UserRepository userRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(User user) {		
		return userRepository.save(user);
	}

	@Override
	public void registerUser(RegisterRequestDto dto) {
		User user = userMapper.toUserEntity(dto);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())
			.ifPresent(existingUser -> {
				throw new IllegalArgumentException("Username or Email already exists");
			});
		userRepository.save(user);
		
	}

	@Override
	public User loadUserByUsernameOrEmail(String usernameOrEmail) {
		if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
			throw new IllegalArgumentException("Username or email cannot be null or empty");
		}
		
		Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail,usernameOrEmail);
		return user.orElseThrow(() -> new RuntimeException("User not found with username or email: " + usernameOrEmail));
	}

	@Override
	public User getUserById(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("User ID cannot be null or empty");
		}
		
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("User not found with ID: " + userId);
		}
		
		// Do something with the user, e.g., return it or process it further
		User foundUser = user.get();
		System.out.println("Found user: " + foundUser);
		return foundUser;
	}

	@Override
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
			throw new IllegalArgumentException("Username or email cannot be null or empty");
		}
		
		Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		return user.orElseThrow(() -> new RuntimeException("User not found with username or email: " + usernameOrEmail));
	}

	@Override
	public User getCurrentUser() {
//		getusername from context
		String username = SecurityContextHolder.getContext().getAuthentication()
			.getName();
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Current user not found");
		}
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isEmpty()) {
			throw new RuntimeException("Current user not found with username: " + username);
		}
		return user.get();
	}

	
}

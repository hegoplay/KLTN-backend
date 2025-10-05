package iuh.fit.se.services.user_service.serviceImpl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import iuh.fit.se.entity.GlobalConfiguration;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.errorHandler.ConflictException;
import iuh.fit.se.repository.GlobalConfigurationRepository;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;
import iuh.fit.se.services.user_service.dto.UpdatePasswordRequestDto;
import iuh.fit.se.services.user_service.dto.UserChangeRoleRequestDto;
import iuh.fit.se.services.user_service.dto.UserUpdateInfoRequestDto;
import iuh.fit.se.services.user_service.dto.UserUpdatePasswordDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	UserRepository userRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	GlobalConfigurationRepository globalConfigurationRepo;
	
	private static final int MAX_LEADER_PER_COURSE = 1;
	private static final int STUDENT_ID_LENGTH = 6;

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public void registerUser(RegisterRequestDto dto) {
		User user = userMapper.toUserEntity(dto);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository
			.findByUsernameOrEmail(user.getUsername(), user.getEmail())
			.ifPresent(existingUser -> {
				throw new IllegalArgumentException(
					"Username or Email already exists");
			});
		userRepository.save(user);

	}

	@Override
	public User loadUserByUsernameOrEmail(String usernameOrEmail) {
		if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
			throw new IllegalArgumentException(
				"Username or email cannot be null or empty");
		}

		Optional<User> user = userRepository
			.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		return user
			.orElseThrow(() -> new RuntimeException(
				"User not found with username or email: " + usernameOrEmail));
	}

	@Override
	public User getUserById(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new IllegalArgumentException(
				"User ID cannot be null or empty");
		}

		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("User not found with ID: " + userId);
		}

		// Do something with the user, e.g., return it or process it further
		User foundUser = user.get();
		log.info("Found user: " + foundUser);
		return foundUser;
	}

	@Override
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
			throw new IllegalArgumentException(
				"Username or email cannot be null or empty");
		}

		Optional<User> user = userRepository
			.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		return user
			.orElseThrow(() -> new RuntimeException(
				"User not found with username or email: " + usernameOrEmail));
	}

	@Override
	public User getCurrentUser() {
		// getusername from context
		String username = SecurityContextHolder
			.getContext()
			.getAuthentication()
			.getName();
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Current user not found");
		}
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isEmpty()) {
			throw new RuntimeException(
				"Current user not found with username: " + username);
		}
		return user.get();
	}

	@Override
	public void updateMyPassword(String userId, UpdatePasswordRequestDto dto) {
		User user = userRepository
			.findById(userId)
			.orElseThrow(() -> new RuntimeException(
				"User not found with ID: " + userId));
		if (!passwordEncoder
			.matches(dto.getOldPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Old password is incorrect");
		}
		if (!dto
			.getNewPassword()
			.equalsIgnoreCase(dto.getConfirmNewPassword())) {
			throw new IllegalArgumentException(
				"New password and confirm new password do not match");
		}
		updateUserPassword(user, dto.getNewPassword());
	}

	@Override
	public void updateUserPassword(User user, String newPassword) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	public void leaderUpdateUserPassword(String userId,
		UserUpdatePasswordDto newPassword) {
		User user = userRepository
			.findById(userId)
			.orElseThrow(() -> new RuntimeException(
				"User not found with ID: " + userId));
		updateUserPassword(user, newPassword.getNewPassword());
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void leaderChangeUserRole(String userId,
		UserChangeRoleRequestDto newRole) {
		User user = userRepository
			.findById(userId)
			.orElseThrow(() -> new RuntimeException(
				"User not found with ID: " + userId));
		if (user.isAdmin()) {
			throw new IllegalArgumentException(
				"Cannot change role of an admin user");
		}
		if (newRole.getNewRole() == UserRole.ADMIN) {
			throw new IllegalArgumentException(
				"Cannot assign ADMIN role. Only ADMIN can assign this role");
		}
		if (newRole.getNewRole() == UserRole.LEADER) {
			Specification<User> leaderSpec = (root, query, criteriaBuilder) -> {
				return criteriaBuilder.equal(root.get("role"), UserRole.LEADER);
			};
			if (user.getStudentId() != null) {
				String courses = user.getStudentId().substring(0, user.getStudentId().length() - STUDENT_ID_LENGTH);
				leaderSpec = leaderSpec.and((root, query, criteriaBuilder) -> {
					return criteriaBuilder.like(root.get("studentId"),
						courses + "%");
				});
			}
			else {
				throw new IllegalArgumentException(
					"Cannot assign LEADER role to user without student ID");
			}
			Optional<User> currentLeaderUser = userRepository.findOne(leaderSpec);
			if (currentLeaderUser.isPresent()) {
				User currentLeader = currentLeaderUser.get();
				if (newRole.isAccepted()) {
					currentLeader.setRole(UserRole.MEMBER);
					userRepository.save(currentLeader);
				}
				else {
					throw new ConflictException(
						"Cannot assign LEADER role. There is already a LEADER in the course: "
							+ currentLeader.getStudentId() + ". If want to change, send accepted to true");
					
				}
			}
		}
		user.setRole(newRole.getNewRole());
		userRepository.save(user);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void resetAllAttendancePoint() {
		userRepository.resetAllAttendancePoint();
		globalConfigurationRepo
			.findByConfigKey(GlobalConfiguration.KEY_LAST_RESET_POINT_TIME)
			.orElseThrow(
				() -> new RuntimeException("Global configuration not found"))
			.setConfigValueFromDateTime(java.time.LocalDateTime.now());

	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public void resetAllContributionPoint() {
		userRepository.resetAllAttendancePoint();
		globalConfigurationRepo
			.findByConfigKey(GlobalConfiguration.KEY_LAST_RESET_POINT_TIME)
			.orElseThrow(
				() -> new RuntimeException("Global configuration not found"))
			.setConfigValueFromDateTime(java.time.LocalDateTime.now());
	}

	@Override
	public User getUserByKeyword(String keyword) {
		User user = userRepository
			.findByUsernameOrEmail(keyword, keyword)
			.orElse(null);
		if (user == null) {
			user = userRepository.findById(keyword).orElse(null);
		}
		return user;
	}

	@Override
	public User updateUserInfo(String userId, UserUpdateInfoRequestDto dto) {

		User user = userRepository
			.findById(userId)
			.orElseThrow(() -> new RuntimeException(
				"User not found with ID: " + userId));

		userMapper.mapToUser(dto, user);

		User save = userRepository.save(user);

		return save;
	}

	@Override
	public Page<User> searchUsers(String keyword, Pageable pageable,
		UserRole role) {
		log.info("role: {}", role);
		Specification<User> userSpec = Specification.unrestricted();
		if (role != null) {
			userSpec = userSpec.and((root, query, criteriaBuilder) -> {
				return criteriaBuilder.equal(root.get("role"), role);
			});
		}
		if (keyword != null && keyword.isBlank()) {
			userSpec = userSpec.and((root, query, criteriaBuilder) -> {
				return criteriaBuilder
					.like(root.get("username"), "%" + keyword + "%");
			});
			userSpec = userSpec.and((root, query, criteriaBuilder) -> {
				return criteriaBuilder
					.like(root.get("fullName"), "%" + keyword + "%");
			});
			userSpec = userSpec.and((root, query, criteriaBuilder) -> {
				return criteriaBuilder.equal(root.get("id"), keyword);
			});
		}
		return userRepository.findAll(userSpec, pageable);
	}

}

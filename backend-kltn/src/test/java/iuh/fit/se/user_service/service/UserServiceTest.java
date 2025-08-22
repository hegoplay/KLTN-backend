package iuh.fit.se.user_service.service;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.UserService;

// UserServiceTest.java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void whenValidUserId_thenUserShouldBeFound() {
        // Arrange
        User mockUser = User
        	.builder()
        	.id("abc")
        	.username("testuser")
        	.build();
        when(userRepository.findById("abc")).thenReturn(Optional.of(mockUser));
        
        // Act
//        User found = userService.getUserById("abc").orElse(null);
        
        // Assert
//        assertThat(found.getUsername()).isEqualTo("testuser");
    }
}
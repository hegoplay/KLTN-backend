package iuh.fit.se.user_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import iuh.fit.se.config.SecurityConfig;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.services.user_service.controller.AuthController;
import iuh.fit.se.services.user_service.dto.LoginRequestDto;
import iuh.fit.se.services.user_service.dto.LoginResponseDto;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;

// UserControllerTest.java
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // Import lớp cấu hình Security của bạn
//@SpringBootTest
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper; // Spring tự inject
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Spring tự inject
    
    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;
    
    @Test
    void testLogin() throws Exception {
        User mockUser = User.builder()
			.userId("testuser")
			.username("testuser")
			.password(passwordEncoder.encode("password"))
			.fullName("Test User")
			.role(UserRole.MEMBER)
			.build();
        
        String mockToken = "mock-jwt-token";
        
        when(userService.loadUserByUsernameOrEmail("testuser")).thenReturn(mockUser);
        when(jwtTokenUtil.generateToken(mockUser)).thenReturn(mockToken);
        
        LoginRequestDto request = new LoginRequestDto("testuser", "password");
        String requestBody = objectMapper.writeValueAsString(request);
        
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType("application/json").content(requestBody))
        
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("testuser"))
               .andExpect(jsonPath("$.accessToken").exists())
               .andExpect(jsonPath("$.accessToken").isNotEmpty())
               .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        
        LoginResponseDto value = objectMapper.readValue(responseJson, LoginResponseDto.class);
        
        log.info("Response: {}", value);

    }
    
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
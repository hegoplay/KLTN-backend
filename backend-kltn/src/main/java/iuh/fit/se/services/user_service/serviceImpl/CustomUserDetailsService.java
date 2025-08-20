package iuh.fit.se.services.user_service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import iuh.fit.se.services.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Primary
public class CustomUserDetailsService implements UserDetailsService {
    
	
	private static final Logger log = LoggerFactory
		.getLogger(CustomUserDetailsService.class);
	
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.info("Loading user by username: {}", username);
        return userRepository.findByUsernameOrEmail(username,username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    
}
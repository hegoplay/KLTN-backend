package iuh.fit.se.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import iuh.fit.se.filter.JwtAuthenticationFilter;
import iuh.fit.se.filter.ThreadLocalCleanupFilter;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	// @Value("${frontend.url}")
	// private String frontendUrl;

	@Autowired
	private JwtAuthenticationFilter jwtFilter;

	@Bean
	SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		ThreadLocalCleanupFilter cleanupFilter
	) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", // ✅ Quan
																		// trọng
					"/v3/api-docs", // ✅ Quan trọng
					"/v3/api-docs/**", // ✅ Quan trọng
					"/api-docs/**", "/swagger-resources/**",
					"/swagger-resources", "/webjars/**", "/configuration/ui",
					"/configuration/security", "/favicon.ico")
				.permitAll()
				.requestMatchers("/api/auth/**")
				.permitAll()
				.requestMatchers("/api/leader/**")
				.hasAnyRole("ADMIN", "LEADER") // only allow ADMIN and LEADER
				.requestMatchers("/api/public/**")
				.permitAll() // Allow public access to posts
				.requestMatchers("/actuator/**")
				.permitAll() // Allow public access to actuator endpoints
				.anyRequest()
				.authenticated() // All other requests require authentication
			)
			.sessionManagement(sess -> sess
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http
			.addFilterBefore(jwtFilter,
				UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(cleanupFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("*")); // Allow all
																// origins
		configuration
			.setAllowedMethods(Arrays
				.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Allow
																				// all
																				// methods
		configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all
																// headers
		configuration.setAllowCredentials(false); // Set to false since we're
													// allowing all origins
		configuration
			.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply to all
																// paths
		return source;
	}

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http)
		throws Exception {
		return http.getSharedObject(AuthenticationManager.class);
	}

	@Bean
	AuthenticationProvider authenticationProvider(
		UserDetailsService service,
		PasswordEncoder passwordEncoder
	) {
		log
			.info(
				"Creating DaoAuthenticationProvider with custom UserDetailsService and PasswordEncoder");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
			service);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}
}
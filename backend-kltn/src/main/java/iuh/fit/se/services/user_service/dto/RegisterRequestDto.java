package iuh.fit.se.services.user_service.dto;

import java.time.LocalDate;

import iuh.fit.se.validator.BirthDateBeforeToday;
import jakarta.validation.constraints.Email;

public record RegisterRequestDto(
	@jakarta.validation.constraints.NotBlank(message = "Username is required")
	@jakarta.validation.constraints.Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
	String username, 
	@jakarta.validation.constraints.NotBlank(message = "Password is required")
	@jakarta.validation.constraints.Size(min = 4, max = 64, message = "Password must be between 4 and 64 characters")
	String password,
	@jakarta.validation.constraints.NotBlank(message = "Full name is required")
	String fullName,
	
	@Email(message = "Invalid email format")
	String email,
	
	String studetId,
	
	@BirthDateBeforeToday
	LocalDate dateOfBirth
	
	) {
	
}

package iuh.fit.se.services.user_service.dto;

import java.time.LocalDate;

import iuh.fit.se.validator.BirthDateBeforeToday;
import iuh.fit.se.validator.CustomPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(
	@jakarta.validation.constraints.NotBlank(
		message = "Username is required") 
	@jakarta.validation.constraints.Size(
			min = 4, max = 20,
			message = "Username must be between 4 and 20 characters") 
	String username,
	@NotNull(message = "Password is required") @CustomPassword(
		message = "Password must be between 8 and 32 characters, and contain at least one uppercase letter or digit") 
	String password,
	@jakarta.validation.constraints.NotBlank(
		message = "Full name is required") 
	String fullName,

	@Email(message = "Invalid email format") 
	String email,

	String studetId,

	@BirthDateBeforeToday 
	LocalDate dateOfBirth
) {

}

package iuh.fit.se.services.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
	@Schema(
        description = "Username or email of the user",
        example = "admin",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	@NotBlank(message = "Username is required") String username,
	@Schema(
		description = "Password of the user",
		example = "admin",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "Password is required") String password
) {
}

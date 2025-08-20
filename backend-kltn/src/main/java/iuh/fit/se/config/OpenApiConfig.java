package iuh.fit.se.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "IUH FIT SE API Documentation",
        version = "1.0.0",
        description = "Documentation for all APIs in IUH FIT SE System",
        contact = @Contact(
            name = "Development Team",
            email = "dev-team@iuh.fit.se"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://staging.api.iuh.fit.se", description = "Staging Server"),
        @Server(url = "https://api.iuh.fit.se", description = "Production Server")
    }
//    security = @SecurityRequirement(name = "bearerAuth") // Áp dụng security mặc định cho tất cả API
)
@SecurityScheme(
    name = "bearerAuth", // Tên phải trùng với @SecurityRequirement
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT", // Định dạng bearer là JWT
    description = "JWT Authorization header using the Bearer scheme. Example: \\\"Authorization: Bearer {token}\\\""
)
public class OpenApiConfig {
}
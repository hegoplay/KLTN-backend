package iuh.fit.se.util;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import iuh.fit.se.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expirationMs}")
	private int jwtExpirationMs;

	private Key secretKey;

	@PostConstruct
	public void init() {
		// Khởi tạo secretKey từ jwtSecret
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(User userDetails) {
		Map<String, Object> claims = new HashMap<>();
		// Thêm các claims tùy chỉnh nếu cần
		claims
			.put("roles",
				userDetails
					.getAuthorities()
					.stream()
					.map(authority -> authority.getAuthority())
					.findFirst()
					.orElse("NONE")); // Mặc định là USER nếu không có quyền nào
		claims.put("userId", userDetails.getId()); // Giả sử username là
														// userId
		claims.put("fullName", userDetails.getFullName());

		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts
			.builder()
			.claims(claims)
			.subject(subject)
			.issuedAt(new Date(System.currentTimeMillis()))
			.issuer("iuh.fit.se") // Thay đổi theo tên ứng dụng của bạn
			.expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
			.signWith(secretKey)
			.compact();
	}
	
	public  String getUserIdFromToken(String token) {
		return Jwts
			.parser()
			.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("userId", String.class);
	}

	public String getUsernameFromToken(String token) {
		return Jwts
			.parser()
			.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}

	public List<String> getRolesFromToken(String token) {
		try {
			Claims payload = Jwts
				.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload();
			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) payload.get("roles", Collection.class).stream()
			    .map(Object::toString)
			    .collect(Collectors.toList());
			return roles;
		} catch (Exception e) {
			return List.of("NONE");
		}
	}

	public boolean validateToken(String token) {
		try {
			Jwts
				.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public long getMilliValidationLeft(String token) {
		try {
			Claims claims = Jwts
				.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload();
			Date expiration = claims.getExpiration();
			return expiration.getTime() - System.currentTimeMillis();
		} catch (Exception e) {
			throw new RuntimeException("Invalid token", e);
		}
			
	}

	public String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // Bỏ "Bearer " prefix
		}
		return null; // Trả về null nếu không có token
	}
	
	public String getUserIdFromRequest(HttpServletRequest request) {
		return getUserIdFromToken(getTokenFromRequest(request));
	}
}
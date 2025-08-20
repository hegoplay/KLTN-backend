package iuh.fit.se.filter;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import iuh.fit.se.services.user_service.serviceImpl.TokenBlacklistService;
import iuh.fit.se.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String secretKey;

	private final JwtTokenUtil jwtTokenUtil;
	private final TokenBlacklistService tokenBlacklistService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		log.info("Processing JWT authentication filter");

		// 1. Kiểm tra và lấy token từ header
		if (authorizationHeader != null
			&& authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring(7); // Bỏ "Bearer " prefix
			try {
				if (tokenBlacklistService.isTokenBlacklisted(token)) {
					throw new JwtException("Token has been blacklisted");
				}
				// 2. Tạo SecretKey từ secret string
				SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

				// 3. Parse và validate JWT
				Claims claims = Jwts
					.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();

				username = claims.getSubject();

				List<SimpleGrantedAuthority> list = List
					.of(new SimpleGrantedAuthority(
						claims.get("roles", String.class)));

				// 4. Tạo Authentication object nếu token hợp lệ
				if (username != null && SecurityContextHolder
					.getContext()
					.getAuthentication() == null) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						username, null, list // Có thể thêm authorities nếu cần
					);
					authToken
						.setDetails(new WebAuthenticationDetailsSource()
							.buildDetails(request));
					SecurityContextHolder
						.getContext()
						.setAuthentication(authToken);
				}
			} catch (Exception e) {
				response
					.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"Invalid or expired JWT token");
				return;
			}
		}

		log
			.info(
				"JWT authentication filter processed successfully for user: {}",
				username);

		// 5. Tiếp tục filter chain
		filterChain.doFilter(request, response);
	}
}
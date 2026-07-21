package com.education.tutoring.management.rest.security;

import com.education.tutoring.management.domain.configuration.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

/**
 * Security filter that intercepts HTTP requests to validate JWT tokens from HttpOnly
 * cookies. Populates the Spring Security context if a valid token is found.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtConfigurationProperties jwtConfig;

	private final SecurityCookieUtil securityCookieUtil;

	public JwtAuthenticationFilter(JwtConfigurationProperties jwtConfig, SecurityCookieUtil securityCookieUtil) {
		this.jwtConfig = jwtConfig;
		this.securityCookieUtil = securityCookieUtil;
	}

	/**
	 * Filters incoming requests to extract and validate the JWT from HTTP cookies.
	 * @param request the incoming HTTP request
	 * @param response the outgoing HTTP response
	 * @param filterChain the chain of filters to pass the request/response to
	 * @throws ServletException if an error occurs during the servlet filtering process
	 * @throws IOException if an I/O error occurs during the filtering process
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		// Cleanly extract the token from cookies using the utility
		final String jwt = securityCookieUtil.extractJwtFromRequest(request);
		final String username;

		// Proceed without authentication if no valid Cookie is present.
		if (jwt == null || jwt.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}

		try {

			SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));

			// Parse and verify token signature
			Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();

			username = claims.getSubject();
			String role = claims.get("role", String.class);

			// Authenticate user in the security context if not already authenticated
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
						List.of(new SimpleGrantedAuthority(role)));
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		catch (Exception e) {
			// Log authentication failure (e.g., expired or malformed token) without
			// halting the chain
			logger.error("Cannot set user authentication from cookie: {}", e);
		}

		filterChain.doFilter(request, response);
	}

}

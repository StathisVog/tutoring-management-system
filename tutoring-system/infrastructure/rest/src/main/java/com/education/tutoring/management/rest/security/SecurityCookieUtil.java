package com.education.tutoring.management.rest.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Utility class responsible for the creation and extraction of HTTP-Only security
 * cookies.
 */
@Component
public class SecurityCookieUtil {

	private static final String JWT_COOKIE_NAME = "jwt_token";

	private static final int JWT_COOKIE_MAX_AGE_SECONDS = 24 * 60 * 60;

	/**
	 * Creates a secure, HTTP-Only cookie containing the JWT token.
	 * @param token the generated JWT token
	 * @return a configured {@link ResponseCookie}
	 */
	public ResponseCookie createJwtCookie(String token) {
		return ResponseCookie.from(JWT_COOKIE_NAME, token)
			.httpOnly(true) // Prevents JavaScript access (XSS protection)
			.secure(false) // Set to 'true' in production with HTTPS
			.path("/") // Accessible across the entire application
			.maxAge(JWT_COOKIE_MAX_AGE_SECONDS)
			.sameSite("Strict") // Prevents Cross-Site Request Forgery (CSRF)
			.build();
	}

	/**
	 * Creates a secure cookie designed to overwrite and immediately delete the existing
	 * JWT cookie.
	 * @return a configured {@link ResponseCookie} with 0 max-age
	 */
	public ResponseCookie deleteJwtCookie() {
		return ResponseCookie.from(JWT_COOKIE_NAME, "")
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(0)
			.sameSite("Strict")
			.build();
	}

	/**
	 * Extracts the JWT token from the incoming HTTP request cookies.
	 * @param request the incoming HTTP request
	 * @return the JWT token string, or null if the cookie is not present
	 */
	public String extractJwtFromRequest(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}

		for (Cookie cookie : request.getCookies()) {
			if (JWT_COOKIE_NAME.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

}

package com.education.tutoring.management.rest.configuration;

import com.education.tutoring.management.rest.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for security-related beans.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Provides a PasswordEncoder bean for hashing passwords.
	 * @return Bcrypt password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				// --- SWAGGER & API DOCS ---
				.requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**")
				.permitAll()
				.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
				.permitAll()

				// --- REST API AUTHENTICATION ---
				.requestMatchers("/api/auth/**")
				.permitAll()

				// --- UI PAGES (PUBLIC) ---
				.requestMatchers(HttpMethod.GET, "/", "/login", "/register", "/courses", "/error")
				.permitAll()

				// --- UI PAGES (SECURED BY ROLE) ---
				.requestMatchers(HttpMethod.GET, "/admin/**")
				.hasAuthority("ROLE_ADMIN")
				.requestMatchers(HttpMethod.GET, "/teacher/**")
				.hasAuthority("ROLE_TEACHER")
				.requestMatchers(HttpMethod.GET, "/student/**")
				.hasAuthority("ROLE_STUDENT")

				// --- REST API STUDENTS ---
				.requestMatchers("/api/students/me/**")
				.hasAuthority("ROLE_STUDENT")

				// --- REST API TEACHERS ---
				.requestMatchers("/api/teachers/**")
				.hasAuthority("ROLE_TEACHER")

				// --- REST API ADMIN ---
				.requestMatchers("/api/admin/**")
				.hasAuthority("ROLE_ADMIN")

				// --- REST API SCHEDULED SLOTS (GLOBAL TIMETABLE) ---
				.requestMatchers(HttpMethod.GET, "/api/slots")
				.authenticated()

				// --- REST API COURSES ---
				.requestMatchers(HttpMethod.GET, "/api/courses")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/courses/*/slots")
				.permitAll()

				// --- REST API COURSES (ADMIN) ---
				.requestMatchers("/api/courses/**")
				.hasAuthority("ROLE_ADMIN")

				// --- REST API USERS ---
				.requestMatchers("/api/users/**")
				.authenticated()

				// --- CATCH-ALL (Safety Net) ---
				.anyRequest()
				.authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}

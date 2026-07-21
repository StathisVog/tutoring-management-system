package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.authentication.AuthenticationResponseDTO;
import com.education.tutoring.management.application.dto.authentication.LoginRequestDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.application.port.in.authentication.LoginUser;
import com.education.tutoring.management.application.port.in.authentication.RegisterStudent;
import com.education.tutoring.management.application.port.in.authentication.RegisterTeacher;
import com.education.tutoring.management.domain.exception.LoginUserException;
import com.education.tutoring.management.domain.exception.RegisterUserException;
import com.education.tutoring.management.rest.adapter.AuthenticationResourceMapper;
import com.education.tutoring.management.rest.model.authentication.LoginRequestResource;
import com.education.tutoring.management.rest.model.authentication.LoginResponseResource;
import com.education.tutoring.management.rest.model.authentication.RegisterStudentResource;
import com.education.tutoring.management.rest.model.authentication.RegisterTeacherResource;
import com.education.tutoring.management.rest.security.SecurityCookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller acting as the primary driving adapter for system authentication and
 * user onboarding. Exposes public endpoints to handle student and teacher registrations,
 * as well as secure login workflows, orchestrating credential verification and the
 * issuance of secure JWTs via HTTP cookies.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Endpoints for user registration and JWT authentication")
public class AuthenticationController {

	private final RegisterStudent registerStudent;

	private final RegisterTeacher registerTeacher;

	private final LoginUser loginUser;

	private final SecurityCookieUtil securityCookieUtil;

	/**
	 * Registers a new student in the system.
	 * @param registerStudentResource the registration payload containing student details
	 * @return the created student data
	 * @throws RegisterUserException if validation fails
	 */
	@Operation(summary = "Register a new student",
			description = "Creates a new student account. The account is inactive by default.")
	@ApiResponse(responseCode = "201", description = "Student successfully created")
	@ApiResponse(responseCode = "400", description = "Invalid input data or username/email already exists")
	@PostMapping("/register/student")
	public ResponseEntity<Void> registerStudent(@RequestBody RegisterStudentResource registerStudentResource)
			throws RegisterUserException {

		RegisterStudentDTO registerStudentDTO = AuthenticationResourceMapper.MAPPER.toDTO(registerStudentResource);

		registerStudent.execute(registerStudentDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Registers a new teacher in the system.
	 * @param registerTeacherResource the registration payload containing teacher details
	 * @return the created teacher data
	 * @throws RegisterUserException if validation fails
	 */
	@Operation(summary = "Register a new teacher",
			description = "Creates a new teacher account. The account is inactive by default.")
	@ApiResponse(responseCode = "201", description = "Teacher successfully created")
	@ApiResponse(responseCode = "400", description = "Invalid input data or username/email already exists")
	@PostMapping("/register/teacher")
	public ResponseEntity<Void> registerTeacher(@RequestBody RegisterTeacherResource registerTeacherResource)
			throws RegisterUserException {

		RegisterTeacherDTO registerTeacherDTO = AuthenticationResourceMapper.MAPPER.toDTO(registerTeacherResource);

		registerTeacher.execute(registerTeacherDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Authenticates a user and generates a JWT. The token is attached to the response as
	 * an HttpOnly cookie for security.
	 * @param loginRequestResource the payload containing username and password
	 * @param response the HTTP response to attach the cookie to
	 * @return an authentication response containing the user's role for UI routing
	 * @throws LoginUserException if credentials are invalid or the account is disabled
	 */
	@Operation(summary = "Authenticate user",
			description = "Validates credentials, sets an HttpOnly cookie with the JWT, and returns the user role.")
	@ApiResponse(responseCode = "200", description = "Successfully authenticated and cookie set")
	@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials or inactive account")
	@PostMapping("/login")
	public ResponseEntity<LoginResponseResource> login(@RequestBody LoginRequestResource loginRequestResource,
			HttpServletResponse response) throws LoginUserException {

		LoginRequestDTO loginRequestDTO = AuthenticationResourceMapper.MAPPER.toDTO(loginRequestResource);

		AuthenticationResponseDTO responseDTO = loginUser.execute(loginRequestDTO);

		ResponseCookie jwtCookie = securityCookieUtil.createJwtCookie(responseDTO.getToken());
		response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

		LoginResponseResource resource = AuthenticationResourceMapper.MAPPER.toResource(responseDTO);

		return ResponseEntity.ok(resource);
	}

	/**
	 * Retrieves the role of the currently authenticated user based on the HttpOnly
	 * cookie. Used by the frontend (navbar) to determine UI state since JS cannot read
	 * the cookie.
	 * @param authentication the injected Spring Security context
	 * @return the user's role, or 401 if not authenticated
	 */
	@Operation(summary = "Get Current User Role", description = "Returns the role of the authenticated user.")
	@GetMapping("/me")
	public ResponseEntity<LoginResponseResource> getCurrentUserStatus(Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String role = authentication.getAuthorities().iterator().next().getAuthority();

		return ResponseEntity.ok(new LoginResponseResource(role));
	}

	/**
	 * Logs out the user by instructing the browser to delete the JWT HttpOnly cookie.
	 * @param response the HTTP response to attach the deletion cookie to
	 * @return 204 No Content
	 */
	@Operation(summary = "Logout user", description = "Clears the HttpOnly JWT cookie from the browser.")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		ResponseCookie deleteCookie = securityCookieUtil.deleteJwtCookie();
		response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

		return ResponseEntity.noContent().build();
	}

}

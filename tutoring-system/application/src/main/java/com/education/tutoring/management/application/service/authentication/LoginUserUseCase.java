package com.education.tutoring.management.application.service.authentication;

import com.education.tutoring.management.application.dto.authentication.AuthenticationResponseDTO;
import com.education.tutoring.management.application.dto.authentication.LoginRequestDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.authentication.LoginUser;
import com.education.tutoring.management.application.port.out.JwtTokenGenerator;
import com.education.tutoring.management.application.port.out.PasswordService;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.LoginUserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for user authentication. Orchestrates user retrieval, status
 * validation, password verification, and JWT generation.
 */
@Slf4j
@UseCase
class LoginUserUseCase implements LoginUser {

	private final UserPersistence userPersistence;

	private final PasswordService passwordService;

	private final JwtTokenGenerator jwtTokenGenerator;

	public LoginUserUseCase(UserPersistence userPersistence, PasswordService passwordService,
			JwtTokenGenerator jwtTokenGenerator) {
		this.userPersistence = userPersistence;
		this.passwordService = passwordService;
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	/**
	 * Executes the login process for a user.
	 * @param loginRequestDTO the payload containing username and raw password
	 * @return the authentication response containing the generated JWT
	 * @throws LoginUserException if authentication fails due to invalid credentials or
	 * inactive account
	 */
	@Override
	public AuthenticationResponseDTO execute(LoginRequestDTO loginRequestDTO) throws LoginUserException {

		log.info("Authentication attempt for user: {}", loginRequestDTO.getUsername());

		// Retrieve user or throw exception if not found
		UserDTO userDTO = userPersistence.findByUsername(loginRequestDTO.getUsername()).orElseThrow(() -> {
			log.warn("Login failed: Username '{}' not found in the system", loginRequestDTO.getUsername());
			return new LoginUserException("Invalid username or password");
		});

		// Ensure the account is active
		if (!userDTO.isEnabled()) {
			log.warn("Login failed: Account for user '{}' is currently inactive", loginRequestDTO.getUsername());
			throw new LoginUserException("Account is not activated yet. Please contact the administrator.");
		}

		// Verify password match against the stored hash
		if (!passwordService.matches(loginRequestDTO.getPassword(), userDTO.getPasswordHash())) {
			log.warn("Login failed: Invalid password provided for user '{}'", loginRequestDTO.getUsername());
			throw new LoginUserException("Invalid username or password");
		}

		// Generate the authentication token
		String token = jwtTokenGenerator.generateToken(userDTO);

		// Format the role for the Frontend JSON response (e.g., ADMIN -> ROLE_ADMIN)
		String formattedRole = "ROLE_" + userDTO.getRole().name();

		log.info("User '{}' successfully authenticated with role: {}", loginRequestDTO.getUsername(), formattedRole);

		return new AuthenticationResponseDTO(token, formattedRole);
	}

}

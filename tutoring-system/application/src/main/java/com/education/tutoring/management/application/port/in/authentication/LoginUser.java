package com.education.tutoring.management.application.port.in.authentication;

import com.education.tutoring.management.application.dto.authentication.AuthenticationResponseDTO;
import com.education.tutoring.management.application.dto.authentication.LoginRequestDTO;
import com.education.tutoring.management.domain.exception.LoginUserException;

/**
 * Interface for authenticating a user into the system.
 */
public interface LoginUser {

	/**
	 * Executes the login process for a user using their credentials.
	 * @param loginRequestDTO the data transfer object containing the username and
	 * password
	 * @return an {@link AuthenticationResponseDTO} containing the generated token and
	 * user role
	 * @throws LoginUserException if the authentication fails (e.g., invalid credentials
	 * or disabled account)
	 */
	AuthenticationResponseDTO execute(LoginRequestDTO loginRequestDTO) throws LoginUserException;

}

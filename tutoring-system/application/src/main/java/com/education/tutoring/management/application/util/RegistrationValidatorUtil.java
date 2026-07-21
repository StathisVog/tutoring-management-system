package com.education.tutoring.management.application.util;

import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.domain.exception.RegisterUserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Validation logic for user registration processes. Validates basic credentials (username
 * and email).
 */
@Slf4j
public class RegistrationValidatorUtil {

	private final UserPersistence userPersistence;

	public RegistrationValidatorUtil(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Validates that the common fields for both student and teacher are present in the
	 * system.
	 * @param username the chosen username
	 * @param email the chosen email address
	 * @param password the chosen password
	 * @param fullName the chosen fullName
	 * @param address the chosen address
	 * @throws RegisterUserException if validation fails or credentials already exist
	 */
	public void validateCredentials(String username, String email, String password, String fullName, String address)
			throws RegisterUserException {

		if (username == null || username.isBlank()) {
			throw new RegisterUserException("Username is required");
		}

		if (userPersistence.existsByUsername(username)) {
			throw new RegisterUserException("Username already exists");
		}

		if (email == null || email.isBlank()) {
			throw new RegisterUserException("Email is required");
		}

		if (userPersistence.existsByEmail(email)) {
			throw new RegisterUserException("Email already exists");
		}

		if (password == null || password.isBlank()) {
			throw new RegisterUserException("Password is required");
		}

		if (fullName == null || fullName.isBlank()) {
			throw new RegisterUserException("Full name is required");
		}

		if (address == null || address.isBlank()) {
			throw new RegisterUserException("Address is required");
		}
	}

}

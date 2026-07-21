package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when user authentication fails.
 */
public class LoginUserException extends Exception {

	public LoginUserException(String message) {
		super(message);
	}

}

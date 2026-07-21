package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when user registration fails.
 */
public class RegisterUserException extends Exception {

	public RegisterUserException(String message) {
		super(message);
	}

}

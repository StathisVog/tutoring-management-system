package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when validation fails during a user profile update.
 */
public class UpdateUserException extends Exception {

	public UpdateUserException(String message) {
		super(message);
	}

}

package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when a business rule is violated (e.g., self-deletion).
 */
public class IllegalOperationException extends Exception {

	public IllegalOperationException(String message) {
		super(message);
	}

}

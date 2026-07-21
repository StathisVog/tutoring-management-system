package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when an operation is attempted on an enrollment that is in an invalid
 * state for that specific action.
 */
public class InvalidEnrollmentStateException extends RuntimeException {

	public InvalidEnrollmentStateException(String message) {
		super(message);
	}

}

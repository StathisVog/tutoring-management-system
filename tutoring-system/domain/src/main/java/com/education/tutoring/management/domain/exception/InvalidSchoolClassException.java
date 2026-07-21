package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when a provided string value does not match any known school class.
 */
public class InvalidSchoolClassException extends RuntimeException {

	public InvalidSchoolClassException(String message) {
		super(message);
	}

}

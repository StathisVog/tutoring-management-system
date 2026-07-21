package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when a student attempts to enroll in a slot they are already enrolled
 * in.
 */
public class AlreadyEnrolledException extends RuntimeException {

	public AlreadyEnrolledException(String message) {
		super(message);
	}

}

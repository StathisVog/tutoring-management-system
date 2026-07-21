package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when attempting to enroll in a scheduled slot that has reached its
 * capacity.
 */
public class CapacityExceededException extends RuntimeException {

	public CapacityExceededException(String message) {
		super(message);
	}

}

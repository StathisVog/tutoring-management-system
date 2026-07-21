package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 */
public class ResourceNotFoundException extends Exception {

	public ResourceNotFoundException(String message) {
		super(message);
	}

}

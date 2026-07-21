package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when attempting to create a new course, or update an existing one,
 * using a title that already exists in the system.
 */
public class CourseAlreadyExistsException extends Exception {

	public CourseAlreadyExistsException(String message) {
		super(message);
	}

}

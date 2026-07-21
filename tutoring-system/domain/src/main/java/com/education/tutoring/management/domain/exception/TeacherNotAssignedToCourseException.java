package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when attempting to schedule a slot for a teacher who is not assigned
 * to the specified course.
 */
public class TeacherNotAssignedToCourseException extends Exception {

	public TeacherNotAssignedToCourseException(String message) {
		super(message);
	}

}

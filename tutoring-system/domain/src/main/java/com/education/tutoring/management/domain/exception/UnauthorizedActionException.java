package com.education.tutoring.management.domain.exception;

/**
 * Exception thrown when a user attempts to perform an action on a resource they do not
 * own or have permission to modify.
 */
public class UnauthorizedActionException extends Exception {

	public UnauthorizedActionException(String message) {
		super(message);
	}

}

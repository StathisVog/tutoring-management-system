package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for a student requesting to drop an enrollment.
 */
public interface RequestEnrollmentDrop {

	/**
	 * Executes the process of requesting an enrollment drop.
	 * @param username the username of the student making the request
	 * @param enrollmentId the ID of the enrollment to be dropped
	 * @throws ResourceNotFoundException if the enrollment is not found
	 */
	void execute(String username, Long enrollmentId) throws ResourceNotFoundException, UnauthorizedActionException;

}

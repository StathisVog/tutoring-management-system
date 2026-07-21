package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for an administrator to reject a student's pending enrollment request.
 */
public interface RejectEnrollment {

	/**
	 * Executes the rejection process for an enrollment request, resulting in the hard
	 * deletion of the pending enrollment record from the database.
	 * @param enrollmentId the ID of the enrollment request to be rejected
	 * @throws ResourceNotFoundException if the enrollment is not found
	 */
	void execute(Long enrollmentId) throws ResourceNotFoundException;

}

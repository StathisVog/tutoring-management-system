package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for an administrator to approve a student's pending enrollment request.
 */
public interface ApproveEnrollmentRequest {

	/**
	 * Executes the approval process for a pending enrollment request, officially
	 * registering the student into the scheduled slot and changing the status to ACTIVE.
	 * @param enrollmentId the unique identifier of the enrollment request to approve
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found in
	 * the system
	 */
	void execute(Long enrollmentId) throws ResourceNotFoundException;

}

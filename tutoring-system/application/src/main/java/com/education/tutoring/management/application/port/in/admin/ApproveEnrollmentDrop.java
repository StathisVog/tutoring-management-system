package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for an administrator to approve a student's enrollment drop request.
 */
public interface ApproveEnrollmentDrop {

	/**
	 * Executes the approval process for a drop request, resulting in the removal of the
	 * enrollment.
	 * @param enrollmentId the ID of the enrollment to be dropped
	 * @throws ResourceNotFoundException if the enrollment is not found
	 */
	void execute(Long enrollmentId) throws ResourceNotFoundException;

}

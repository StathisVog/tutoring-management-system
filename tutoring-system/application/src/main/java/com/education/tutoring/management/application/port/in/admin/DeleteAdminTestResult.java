package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a specific student's test result.
 */
public interface DeleteAdminTestResult {

	/**
	 * Executes the case for permanently deleting a single nested test result.
	 * @param testId the ID of the parent test
	 * @param testResultId the ID of the test result (graded paper) to remove
	 * @throws ResourceNotFoundException if the parent test or the nested result cannot be
	 * found
	 */
	void execute(Long testId, Long testResultId) throws ResourceNotFoundException;

}

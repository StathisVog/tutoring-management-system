package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a test and its nested results.
 */
public interface DeleteAdminTest {

	/**
	 * Executes the case for permanently deleting a test.
	 * @param testId the ID of the test to delete
	 * @throws ResourceNotFoundException if the test cannot be found
	 */
	void execute(Long testId) throws ResourceNotFoundException;

}

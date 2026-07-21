package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateTestDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively updating a test and its associated student results.
 */
public interface UpdateAdminTest {

	/**
	 * Executes the case for updating a test via an administrative master override.
	 * @param testId the ID of the test to update
	 * @param adminUpdateTestDTO the requested changes (metadata and nested grades)
	 * @throws ResourceNotFoundException if the test is missing
	 * @throws IllegalOperationException if structural integrity or security checks fail
	 */
	void execute(Long testId, AdminUpdateTestDTO adminUpdateTestDTO)
			throws ResourceNotFoundException, IllegalOperationException;

}

package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.DeleteAdminTestResult;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a specific test result.
 */
@Slf4j
@UseCase
class DeleteAdminTestResultUseCase implements DeleteAdminTestResult {

	private final AdminPersistence adminPersistence;

	public DeleteAdminTestResultUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the use case for permanently deleting a specific student's test result
	 * from a parent test.
	 * @param testId the ID of the parent test
	 * @param testResultId the ID of the test result to delete
	 * @throws ResourceNotFoundException if either the test or the test result cannot be
	 * found
	 */
	@Override
	public void execute(Long testId, Long testResultId) throws ResourceNotFoundException {

		log.warn("Admin is permanently deleting test result ID: {} from parent test ID: {}.", testResultId, testId);

		adminPersistence.deleteTestResult(testId, testResultId);
	}

}

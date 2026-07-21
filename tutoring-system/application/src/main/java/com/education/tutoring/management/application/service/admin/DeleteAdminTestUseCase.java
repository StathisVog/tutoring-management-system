package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.DeleteAdminTest;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a scheduled test and its
 * associated results.
 */
@Slf4j
@UseCase
class DeleteAdminTestUseCase implements DeleteAdminTest {

	private final AdminPersistence adminPersistence;

	public DeleteAdminTestUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the use case for permanently deleting a test.
	 * @param testId the unique identifier of the test to delete
	 * @throws ResourceNotFoundException if the test cannot be found in the system
	 */
	@Override
	public void execute(Long testId) throws ResourceNotFoundException {

		log.warn("Admin is permanently deleting test ID: {} and all its associated student results.", testId);

		adminPersistence.deleteTest(testId);
	}

}

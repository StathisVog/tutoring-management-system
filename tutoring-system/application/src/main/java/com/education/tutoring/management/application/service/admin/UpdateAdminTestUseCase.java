package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateTestDTO;
import com.education.tutoring.management.application.port.in.admin.UpdateAdminTest;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively updating a scheduled test and its
 * associated results.
 */
@Slf4j
@UseCase
class UpdateAdminTestUseCase implements UpdateAdminTest {

	private final AdminPersistence adminPersistence;

	public UpdateAdminTestUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the forceful update of a test and specific student results by an
	 * administrator.
	 * @param testId the unique identifier of the test to update
	 * @param adminUpdateTestDTO the new data for the test and its results
	 * @throws ResourceNotFoundException if the test or any of the specified results
	 * cannot be found
	 * @throws IllegalOperationException if the update violates grading or scheduling
	 * rules
	 */
	@Override
	public void execute(Long testId, AdminUpdateTestDTO adminUpdateTestDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		int resultsCount = adminUpdateTestDTO.getResultsToUpdate() != null
				? adminUpdateTestDTO.getResultsToUpdate().size() : 0;

		log.info("Admin is forcefully updating test ID: {} (New Date: {}, Modifying {} student results)", testId,
				adminUpdateTestDTO.getDate(), resultsCount);

		adminPersistence.updateTest(testId, adminUpdateTestDTO);
	}

}

package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.ApproveEnrollmentDrop;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively approving a student's enrollment drop
 * request.
 */
@Slf4j
@UseCase
class ApproveEnrollmentDropUseCase implements ApproveEnrollmentDrop {

	private final AdminPersistence adminPersistence;

	public ApproveEnrollmentDropUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the approval of an enrollment drop, resulting in the removal of the
	 * enrollment.
	 * @param enrollmentId the ID of the enrollment request to be dropped
	 * @throws ResourceNotFoundException if the enrollment request is not found
	 */
	@Override
	public void execute(Long enrollmentId) throws ResourceNotFoundException {

		adminPersistence.approveDrop(enrollmentId);
		log.info("Successfully approved drop request for enrollment ID: {}", enrollmentId);

	}

}

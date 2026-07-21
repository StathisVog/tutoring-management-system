package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.RejectEnrollment;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively rejecting a student's enrollment request.
 */
@Slf4j
@UseCase
class RejectEnrollmentUseCase implements RejectEnrollment {

	private final AdminPersistence adminPersistence;

	public RejectEnrollmentUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the rejection process for a pending enrollment request.
	 * @param enrollmentId the unique identifier of the enrollment request to reject
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found
	 */
	@Override
	public void execute(Long enrollmentId) throws ResourceNotFoundException {

		adminPersistence.rejectEnroll(enrollmentId);
		log.info("Successfully rejected enrollment request for enrollment ID: {}", enrollmentId);
	}

}

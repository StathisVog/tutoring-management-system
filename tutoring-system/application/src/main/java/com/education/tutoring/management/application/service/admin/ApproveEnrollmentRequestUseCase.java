package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.ApproveEnrollmentRequest;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively approving a student's pending enrollment
 * request.
 */
@Slf4j
@UseCase
class ApproveEnrollmentRequestUseCase implements ApproveEnrollmentRequest {

	private final AdminPersistence adminPersistence;

	public ApproveEnrollmentRequestUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the approval process for a pending enrollment, officially registering the
	 * student into the class.
	 * @param enrollmentId the unique identifier of the enrollment request to approve
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found
	 */
	@Override
	public void execute(Long enrollmentId) throws ResourceNotFoundException {

		adminPersistence.approveEnroll(enrollmentId);
		log.info("Successfully approved enrollment request for enrollment ID: {}", enrollmentId);

	}

}

package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.application.port.in.admin.GetPendingEnrollRequests;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all pending enrollment requests awaiting
 * administrative approval.
 */
@Slf4j
@UseCase
class GetPendingEnrollRequestsUseCase implements GetPendingEnrollRequests {

	private final AdminPersistence adminPersistence;

	public GetPendingEnrollRequestsUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of all pending enrollment requests.
	 * @return a list of {@link EnrollmentResponseDTO} representing the pending
	 * enrollments
	 */
	@Override
	public List<EnrollmentResponseDTO> execute() {

		log.debug("Admin requested to fetch all pending enrollment requests.");
		return adminPersistence.getPendingEnrollRequests();
	}

}

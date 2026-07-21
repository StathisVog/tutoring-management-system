package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.application.port.in.admin.GetPendingDropRequests;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all pending enrollment drop requests awaiting
 * administrative approval.
 */
@Slf4j
@UseCase
class GetPendingDropRequestsUseCase implements GetPendingDropRequests {

	private final AdminPersistence adminPersistence;

	public GetPendingDropRequestsUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of all pending drop requests.
	 * @return a list of {@link EnrollmentResponseDTO} representing the pending drops
	 */
	@Override
	public List<EnrollmentResponseDTO> execute() {

		log.debug("Admin requested to fetch all pending drop requests.");
		return adminPersistence.getPendingDropRequests();
	}

}

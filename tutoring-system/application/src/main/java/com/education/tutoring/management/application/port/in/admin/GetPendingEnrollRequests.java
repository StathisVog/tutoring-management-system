package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;

import java.util.List;

/**
 * Interface for retrieving all pending student enrollment requests.
 */
public interface GetPendingEnrollRequests {

	/**
	 * Executes the retrieval of all student enrollments that are currently in a pending
	 * state, waiting for administrative approval.
	 * @return a list of {@link EnrollmentResponseDTO} containing the details of the
	 * pending enrollments
	 */
	List<EnrollmentResponseDTO> execute();

}

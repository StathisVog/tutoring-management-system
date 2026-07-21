package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;

import java.util.List;

/**
 * Interface for retrieving all pending enrollment drop requests submitted by students.
 */
public interface GetPendingDropRequests {

	/**
	 * Executes the retrieval of all pending drop requests.
	 * @return a list of {@link EnrollmentResponseDTO}
	 */
	List<EnrollmentResponseDTO> execute();

}

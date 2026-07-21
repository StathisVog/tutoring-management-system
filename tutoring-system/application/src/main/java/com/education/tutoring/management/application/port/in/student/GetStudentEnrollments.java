package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.domain.util.EnrollmentStatus;

import java.util.List;

/**
 * Interface for retrieving a student's own enrollments.
 */
public interface GetStudentEnrollments {

	/**
	 * Executes the retrieval of enrollments for the specified student username,
	 * optionally filtered by status.
	 * @param username the username of the student
	 * @param statuses an optional list of statuses to filter by
	 * @return a list of {@link EnrollmentResponseDTO}
	 */
	List<EnrollmentResponseDTO> execute(String username, List<EnrollmentStatus> statuses);

}

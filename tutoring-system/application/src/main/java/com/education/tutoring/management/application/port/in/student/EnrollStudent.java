package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for enrolling a student into a scheduled class.
 */
public interface EnrollStudent {

	/**
	 * Executes the student enrollment process for a specific scheduled slot.
	 * @param username the username of the student requesting enrollment
	 * @param slotId the ID of the scheduled slot to enroll in
	 * @return an {@link EnrollmentResponseDTO} containing the details of the newly
	 * created enrollment
	 * @throws ResourceNotFoundException if the student or the specified scheduled slot is
	 * not found
	 * @throws IllegalOperationException if the requested scheduled slot overlaps in time
	 * with another class the student is already enrolled in
	 */
	EnrollmentResponseDTO execute(String username, Long slotId)
			throws ResourceNotFoundException, IllegalOperationException;

}

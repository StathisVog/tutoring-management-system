package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.application.port.in.student.EnrollStudent;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for allowing a student to submit an enrollment request for a
 * specific class slot.
 */
@Slf4j
@UseCase
class EnrollStudentUseCase implements EnrollStudent {

	private final StudentPersistence studentPersistence;

	public EnrollStudentUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the enrollment request process for a student.
	 * @param username the username of the requesting student
	 * @param slotId the ID of the scheduled slot to enroll in
	 * @return the created {@link EnrollmentResponseDTO} representing the pending request
	 * @throws ResourceNotFoundException if the student or slot is not found
	 * @throws IllegalOperationException if the enrollment violates business rules (e.g.,
	 * already enrolled, no capacity)
	 */
	@Override
	public EnrollmentResponseDTO execute(String username, Long slotId)
			throws ResourceNotFoundException, IllegalOperationException {

		EnrollmentResponseDTO response = studentPersistence.enrollStudent(username, slotId);
		log.info("Student '{}' successfully submitted an enrollment request for slot ID: {}", username, slotId);

		return response;
	}

}

package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.port.in.student.RequestEnrollmentDrop;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for allowing a student to submit a request to drop an active
 * enrollment.
 */
@Slf4j
@UseCase
class RequestEnrollmentDropUseCase implements RequestEnrollmentDrop {

	private final StudentPersistence studentPersistence;

	public RequestEnrollmentDropUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the drop request process for a specific enrollment.
	 * @param username the username of the requesting student
	 * @param enrollmentId the ID of the enrollment to drop
	 * @throws ResourceNotFoundException if the enrollment cannot be found
	 * @throws UnauthorizedActionException if the student does not own the enrollment
	 */
	@Override
	public void execute(String username, Long enrollmentId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		studentPersistence.requestEnrollmentDrop(username, enrollmentId);
		log.info("Student '{}' successfully requested to drop enrollment ID: {}", username, enrollmentId);
	}

}

package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.application.port.in.student.GetStudentEnrollments;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving a student's enrollments, optionally filtered by
 * their status.
 */
@Slf4j
@UseCase
class GetStudentEnrollmentsUseCase implements GetStudentEnrollments {

	private final StudentPersistence studentPersistence;

	public GetStudentEnrollmentsUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the retrieval of student enrollments.
	 * @param username the username of the student
	 * @param statuses the optional list of {@link EnrollmentStatus} to filter by
	 * @return a list of {@link EnrollmentResponseDTO} representing the student's
	 * enrollments
	 */
	@Override
	public List<EnrollmentResponseDTO> execute(String username, List<EnrollmentStatus> statuses) {

		log.debug("Fetching enrollments for student '{}' with statuses: {}", username,
				statuses != null ? statuses : "ANY");
		return studentPersistence.getMyEnrollments(username, statuses);
	}

}

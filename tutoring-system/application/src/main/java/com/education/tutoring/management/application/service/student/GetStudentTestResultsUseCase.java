package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;
import com.education.tutoring.management.application.port.in.student.GetStudentTestResults;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving a specific student's assigned tests and their
 * corresponding grades.
 */
@Slf4j
@UseCase
class GetStudentTestResultsUseCase implements GetStudentTestResults {

	private final StudentPersistence studentPersistence;

	public GetStudentTestResultsUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the retrieval of test results for a student.
	 * @param username the username of the requesting student
	 * @return a list of {@link StudentTestResponseDTO} containing test details and grades
	 */
	@Override
	public List<StudentTestResponseDTO> execute(String username) {

		log.debug("Retrieving test results for student: {}", username);
		return studentPersistence.getStudentTestResults(username);
	}

}

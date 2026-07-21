package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;

import java.util.List;

/**
 * Interface for retrieving a specific student's assigned tests and their corresponding
 * grades.
 */
public interface GetStudentTestResults {

	/**
	 * Executes the retrieval of a student's assigned tests and their corresponding
	 * results.
	 * @param username the username of the student requesting the data
	 * @return a list of {@link StudentTestResponseDTO} containing the test details
	 */
	List<StudentTestResponseDTO> execute(String username);

}

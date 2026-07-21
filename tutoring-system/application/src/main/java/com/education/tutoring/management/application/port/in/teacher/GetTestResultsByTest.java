package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.TestResultDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

import java.util.List;

/**
 * Interface for retrieving all student results for a specific test.
 */
public interface GetTestResultsByTest {

	/**
	 * Executes the retrieval of test results.
	 * @param teacherUsername the username of the requesting teacher
	 * @param testId the ID of the test
	 * @return a list of {@link TestResultDTO} objects
	 * @throws ResourceNotFoundException if the test is not found
	 * @throws UnauthorizedActionException if the teacher does not own the test
	 */
	List<TestResultDTO> execute(String teacherUsername, Long testId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.TestResultDTO;
import com.education.tutoring.management.application.port.in.teacher.GetTestResultsByTest;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all student results (grades and feedback) for a
 * specific test created by the teacher.
 */
@Slf4j
@UseCase
class GetTestResultsByTestUseCase implements GetTestResultsByTest {

	private final TeacherPersistence teacherPersistence;

	public GetTestResultsByTestUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of test results.
	 * @param teacherUsername the username of the requesting teacher
	 * @param testId the unique identifier of the test
	 * @return a list of {@link TestResultDTO} representing the student grades for the
	 * test
	 * @throws ResourceNotFoundException if the test cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the test
	 */
	@Override
	public List<TestResultDTO> execute(String teacherUsername, Long testId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		log.debug("Fetching test results for test ID: {} requested by teacher '{}'", testId, teacherUsername);

		return teacherPersistence.getTestResultsForTest(teacherUsername, testId);
	}

}

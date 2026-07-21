package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.GradeTestResultDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for grading a specific test result.
 */
public interface GradeTestResult {

	/**
	 * Executes the grading process for a specific student's test result. Ensures that the
	 * teacher applying the grade is the legitimate author of the test.
	 * @param teacherUsername the username of the teacher performing the grading
	 * @param testResultId the unique identifier of the student's test result (the exam
	 * paper)
	 * @param gradeTestResultDTO the data transfer object containing the assigned grade
	 * @throws ResourceNotFoundException if the specified test result or the teacher
	 * cannot be found in the database
	 * @throws UnauthorizedActionException if the requesting teacher is not the creator of
	 * the test associated with this result
	 */
	void execute(String teacherUsername, Long testResultId, GradeTestResultDTO gradeTestResultDTO)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

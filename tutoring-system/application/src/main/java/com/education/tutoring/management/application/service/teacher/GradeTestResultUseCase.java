package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.GradeTestResultDTO;
import com.education.tutoring.management.application.port.in.teacher.GradeTestResult;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for allowing a teacher to grade a student's specific test
 * result.
 */
@Slf4j
@UseCase
class GradeTestResultUseCase implements GradeTestResult {

	private final TeacherPersistence teacherPersistence;

	public GradeTestResultUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the grading process for a specific test result.
	 * @param teacherUsername the username of the grading teacher
	 * @param testResultId the ID of the specific student's test result to grade
	 * @param gradeTestResultDTO the data containing the grade score and optional feedback
	 * @throws ResourceNotFoundException if the test result cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the parent test
	 */
	@Override
	public void execute(String teacherUsername, Long testResultId, GradeTestResultDTO gradeTestResultDTO)
			throws ResourceNotFoundException, UnauthorizedActionException {

		teacherPersistence.updateTestResultGrade(teacherUsername, testResultId, gradeTestResultDTO);
		log.info("Teacher '{}' successfully graded test result ID: {}", teacherUsername, testResultId);
	}

}

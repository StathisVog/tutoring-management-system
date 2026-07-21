package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.TestDTO;
import com.education.tutoring.management.application.port.in.teacher.GetTeacherTests;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving tests created by a specific teacher, optionally
 * filtered by date.
 */
@Slf4j
@UseCase
class GetTeacherTestsUseCase implements GetTeacherTests {

	private final TeacherPersistence teacherPersistence;

	public GetTeacherTestsUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of scheduled tests for the teacher.
	 * @param teacherUsername the username of the requesting teacher
	 * @param fromDate the optional start date of the date range filter
	 * @param toDate the optional end date of the date range filter
	 * @return a list of {@link TestDTO} matching the criteria
	 * @throws ResourceNotFoundException if the teacher cannot be found
	 * @throws IllegalOperationException if the date range logic is invalid
	 */
	@Override
	public List<TestDTO> execute(String teacherUsername, LocalDate fromDate, LocalDate toDate)
			throws ResourceNotFoundException, IllegalOperationException {

		log.debug("Fetching tests for teacher '{}' from {} to {}", teacherUsername, fromDate != null ? fromDate : "ANY",
				toDate != null ? toDate : "ANY");

		return teacherPersistence.getTeacherTests(teacherUsername, fromDate, toDate);
	}

}

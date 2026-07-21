package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.TestDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving tests authored by a specific teacher within a designated date
 * range.
 */
public interface GetTeacherTests {

	/**
	 * Executes the retrieval of the teacher's tests.
	 * @param teacherUsername the username of the requesting teacher
	 * @param fromDate the optional start date of the filtering range
	 * @param toDate the optional end date of the filtering range
	 * @return a list of {@link TestDTO} objects representing the tests found
	 * @throws ResourceNotFoundException if the specified teacher does not exist
	 * @throws IllegalOperationException if the filtering date range is chronologically
	 * invalid
	 */
	List<TestDTO> execute(String teacherUsername, LocalDate fromDate, LocalDate toDate)
			throws ResourceNotFoundException, IllegalOperationException;

}

package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving a student's dynamically generated weekly schedule.
 */
public interface GetStudentSchedule {

	/**
	 * Executes the retrieval of the student's schedule within the specified date range.
	 * Enforces constraints such as maximum date ranges and academic year boundaries.
	 * @param studentUsername the username of the student requesting the schedule
	 * @param startDate the desired start date (can be null for default behavior)
	 * @param endDate the desired end date (can be null for default behavior)
	 * @return a chronologically ordered list of {@link StudentDailyScheduleDTO}
	 * @throws ResourceNotFoundException if the requesting student cannot be found
	 * @throws IllegalOperationException if the date range exceeds 31 days or falls
	 * outside the current academic year
	 */
	List<StudentDailyScheduleDTO> execute(String studentUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException;

}

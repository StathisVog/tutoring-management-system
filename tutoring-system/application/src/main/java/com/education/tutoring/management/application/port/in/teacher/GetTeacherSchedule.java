package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.TeacherDailyScheduleDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving a teacher's dynamically generated weekly schedule.
 */
public interface GetTeacherSchedule {

	/**
	 * Executes the retrieval of the teacher's schedule within the specified date range.
	 * Enforces constraints such as maximum date ranges and academic year boundaries.
	 * @param teacherUsername the username of the teacher requesting the schedule
	 * @param startDate the desired start date (can be null for default behavior)
	 * @param endDate the desired end date (can be null for default behavior)
	 * @return a chronologically ordered list of {@link TeacherDailyScheduleDTO}
	 * @throws ResourceNotFoundException if the requesting teacher cannot be found
	 * @throws IllegalOperationException if the date range exceeds 31 days or falls
	 * outside the current academic year
	 */
	List<TeacherDailyScheduleDTO> execute(String teacherUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException;

}

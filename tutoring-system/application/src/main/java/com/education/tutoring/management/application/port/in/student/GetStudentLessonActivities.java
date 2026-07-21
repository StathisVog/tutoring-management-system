package com.education.tutoring.management.application.port.in.student;

import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

import java.util.List;

/**
 * Interface for retrieving the lesson activities timeline from a student's perspective.
 */
public interface GetStudentLessonActivities {

	/**
	 * Executes the case for retrieving a student's upcoming lesson activities. Acts as a
	 * "To-Do list" by filtering out past activities and sorting by the nearest upcoming
	 * date.
	 * @param studentUsername the username of the logged-in student
	 * @param slotId optional slot ID to restrict the results to a specific class
	 * @return a chronologically ordered list of {@link StudentLessonActivityDTO}
	 * (ascending)
	 * @throws ResourceNotFoundException if the student resource cannot be resolved
	 * @throws UnauthorizedActionException if the student lacks access to the requested
	 * slot
	 */
	List<StudentLessonActivityDTO> execute(String studentUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

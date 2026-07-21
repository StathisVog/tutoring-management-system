package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.LessonActivityDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

import java.util.List;

/**
 * Interface for retrieving lesson activities logged by a teacher, with optional filtering
 * by class slot.
 */
public interface GetLessonActivities {

	/**
	 * Executes the retrieval of lesson activities for the authenticated teacher. If a
	 * slotId is provided, it filters the lesson activities for that specific class. If
	 * omitted (null), it acts as a global fetch, returning all lesson activities for all
	 * classes taught by the teacher.
	 * @param teacherUsername the username of the teacher requesting the lesson activities
	 * @param slotId the optional unique identifier of the scheduled slot to filter by
	 * @return a chronologically ordered list of {@link LessonActivityDTO} representing
	 * the lesson activities
	 * @throws ResourceNotFoundException if the requested scheduled slot or teacher does
	 * not exist
	 * @throws UnauthorizedActionException if the teacher is not the assigned instructor
	 * for the specified slot
	 */
	List<LessonActivityDTO> execute(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.UpdateLessonActivityDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for updating an existing lesson activity.
 */
public interface UpdateLessonActivity {

	/**
	 * Executes the use case for updating a class log or syllabus plan.
	 * @param teacherUsername the username of the teacher modifying the log
	 * @param activityId the unique identifier of the activity to update
	 * @param updateLessonActivityDTO the payload containing updated info
	 * @throws ResourceNotFoundException if resources are missing
	 * @throws UnauthorizedActionException if security rules fail
	 * @throws IllegalOperationException if chronological rules are broken
	 */
	void execute(String teacherUsername, Long activityId, UpdateLessonActivityDTO updateLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

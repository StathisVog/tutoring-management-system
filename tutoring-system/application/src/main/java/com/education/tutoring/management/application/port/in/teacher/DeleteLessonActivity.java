package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for deleting a lesson activity.
 */
public interface DeleteLessonActivity {

	/**
	 * Executes the case for deleting a lesson activity. Enforces strict historical data
	 * integrity: Only future or present-day plans can be deleted. Past activities are
	 * locked from deletion to preserve the audit trail.
	 * @param teacherUsername the username of the teacher requesting the deletion
	 * @param activityId the ID of the activity to delete
	 * @throws ResourceNotFoundException if the lesson activity is missing
	 * @throws UnauthorizedActionException if the teacher does not own the class
	 * @throws IllegalOperationException if the lesson activity date is in the past
	 */
	void execute(String teacherUsername, Long activityId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for deleting a future or present teacher absence.
 */
public interface DeleteTeacherAbsence {

	/**
	 * Executes the deletion of a teacher's absence (full-day or slot-specific).
	 * @param teacherUsername the username of the requesting teacher
	 * @param absenceId the ID of the absence to delete
	 * @throws ResourceNotFoundException if the teacher or absence is not found
	 * @throws UnauthorizedActionException if the teacher does not own the absence
	 * @throws IllegalOperationException if attempting to delete a historical (past)
	 * absence
	 */
	void execute(String teacherUsername, Long absenceId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

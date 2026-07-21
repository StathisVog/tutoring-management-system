package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.AbsenceRequestDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for updating an existing future or present teacher absence.
 */
public interface UpdateTeacherAbsence {

	/**
	 * Executes the update process for an existing teacher absence. Validates ownership of
	 * both the absence and the (optional) new slot, enforces rules to ensure historical
	 * absences are not modified, and prevents duplicate absence declarations.
	 * @param teacherUsername the username of the teacher requesting the update
	 * @param absenceId the unique identifier of the absence to be modified
	 * @param absenceRequestDTO the data transfer object containing the updated date,
	 * reason, and slotId
	 * @throws ResourceNotFoundException if the specified teacher, absence, or new slot
	 * cannot be found
	 * @throws UnauthorizedActionException if the requesting teacher is not the creator of
	 * the absence or owner of the slot
	 * @throws IllegalOperationException if attempting to modify a historical absence, day
	 * mismatch, or duplicate conflict
	 */
	void execute(String teacherUsername, Long absenceId, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

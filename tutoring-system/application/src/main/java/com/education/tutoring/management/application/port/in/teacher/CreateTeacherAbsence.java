package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.AbsenceRequestDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for a teacher to declare a future or present absence.
 */
public interface CreateTeacherAbsence {

	/**
	 * Executes the declaration of a teacher's absence, supporting both full-day and
	 * slot-specific cancellations.
	 * @param teacherUsername the username of the teacher declaring the absence
	 * @param absenceRequestDTO the details of the absence (date, reason, and optional
	 * slotId)
	 * @throws ResourceNotFoundException if the teacher or the requested scheduled slot
	 * cannot be found
	 * @throws IllegalOperationException if attempting to declare an absence in the past,
	 * if there is a day mismatch, or if an absence already exists
	 * @throws UnauthorizedActionException if the teacher tries to declare an absence for
	 * a slot that belongs to someone else
	 */
	void execute(String teacherUsername, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, IllegalOperationException, UnauthorizedActionException;

}

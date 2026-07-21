package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.CreateLessonActivityDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for creating a new lesson activity entry.
 */
public interface CreateLessonActivity {

	/**
	 * Executes the case for recording or planning a class activity. Enforces the [-14
	 * days, +30 days] operational window.
	 * @param teacherUsername the username of the teacher creating the log
	 * @param slotId the identifier of the target class slot
	 * @param createLessonActivityDTO the payload containing the entry details
	 * @throws ResourceNotFoundException if core resources are missing
	 * @throws UnauthorizedActionException if security restrictions are violated
	 * @throws IllegalOperationException if rules or time boundaries are broken
	 */
	void execute(String teacherUsername, Long slotId, CreateLessonActivityDTO createLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

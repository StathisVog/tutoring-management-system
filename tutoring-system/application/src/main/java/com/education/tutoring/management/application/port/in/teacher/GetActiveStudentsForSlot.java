package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.ActiveStudentDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

import java.util.List;

/**
 * Interface for retrieving the list of active students enrolled in a specific class slot.
 */
public interface GetActiveStudentsForSlot {

	/**
	 * Executes the retrieval of active students for a given slot, ensuring teacher
	 * authorization.
	 * @param teacherUsername the username of the requesting teacher
	 * @param slotId the ID of the scheduled slot
	 * @return a list of {@link ActiveStudentDTO}
	 */
	List<ActiveStudentDTO> execute(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

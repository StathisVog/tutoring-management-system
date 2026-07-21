package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateTeacherAbsenceDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively updating a teacher's absence.
 */
public interface UpdateAdminTeacherAbsence {

	/**
	 * Executes the case for updating a teacher absence via master override.
	 * @param absenceId the ID of the teacher absence to update
	 * @param updateDTO the requested changes
	 * @throws ResourceNotFoundException if the teacher absence or requested slot is
	 * missing
	 * @throws IllegalOperationException if structural integrity checks fail
	 */
	void execute(Long absenceId, AdminUpdateTeacherAbsenceDTO updateDTO)
			throws ResourceNotFoundException, IllegalOperationException;

}

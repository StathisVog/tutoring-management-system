package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a teacher's absence record.
 */
public interface DeleteAdminTeacherAbsence {

	/**
	 * Executes the case for permanently deleting a teacher absence.
	 * @param absenceId the ID of the absence to delete
	 * @throws ResourceNotFoundException if the teacher absence cannot be found
	 */
	void execute(Long absenceId) throws ResourceNotFoundException;

}

package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateLessonActivityDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively overriding and updating a lesson activity.
 */
public interface UpdateAdminLessonActivity {

	/**
	 * Executes the case for updating a lesson activity via an administrative master
	 * override.
	 * @param activityId the ID of the activity to update
	 * @param adminUpdateLessonActivityDTO the requested changes
	 * @throws ResourceNotFoundException if the activity is missing
	 * @throws IllegalOperationException if structural integrity checks fail
	 */
	void execute(Long activityId, AdminUpdateLessonActivityDTO adminUpdateLessonActivityDTO)
			throws ResourceNotFoundException, IllegalOperationException;

}

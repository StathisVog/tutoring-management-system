package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a lesson activity.
 */
public interface DeleteAdminLessonActivity {

	/**
	 * Executes the use case for permanently deleting a lesson activity.
	 * @param activityId the ID of the activity to delete
	 * @throws ResourceNotFoundException if the activity cannot be found
	 */
	void execute(Long activityId) throws ResourceNotFoundException;

}

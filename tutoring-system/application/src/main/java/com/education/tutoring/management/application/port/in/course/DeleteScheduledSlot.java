package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a scheduled class slot from a course.
 */
public interface DeleteScheduledSlot {

	/**
	 * Executes the deletion of a scheduled slot.
	 * @param courseId the ID of the course
	 * @param slotId the ID of the scheduled slot to delete
	 * @throws ResourceNotFoundException if the course or slot is not found
	 */
	void execute(Long courseId, Long slotId) throws ResourceNotFoundException;

}

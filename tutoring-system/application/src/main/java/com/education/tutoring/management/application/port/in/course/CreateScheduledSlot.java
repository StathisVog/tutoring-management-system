package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.TeacherNotAssignedToCourseException;

/**
 * Interface for creating and scheduling a new class slot for a specific course.
 */
public interface CreateScheduledSlot {

	/**
	 * Executes the creation of a scheduled slot.
	 * @param courseId the ID of the course
	 * @param scheduledSlotDTO the data transfer object containing the slot info
	 * @return the created {@link ScheduledSlotDTO}
	 * @throws ResourceNotFoundException if the course or teacher is not found
	 * @throws TeacherNotAssignedToCourseException if the teacher is not assigned to the
	 * course
	 * @throws IllegalOperationException if there is a time overlap conflict for either
	 * the teacher or the classroom
	 */
	ScheduledSlotDTO execute(Long courseId, ScheduledSlotDTO scheduledSlotDTO)
			throws ResourceNotFoundException, TeacherNotAssignedToCourseException, IllegalOperationException;

}

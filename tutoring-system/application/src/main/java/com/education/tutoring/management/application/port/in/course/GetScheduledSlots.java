package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for retrieving the scheduled slots of a course.
 */
public interface GetScheduledSlots {

	/**
	 * Executes the retrieval of scheduled slots for a given course.
	 * @param courseId the ID of the course
	 * @return a list of {@link ScheduledSlotDTO}
	 * @throws ResourceNotFoundException if the specified course is not found
	 */
	List<ScheduledSlotDTO> execute(Long courseId) throws ResourceNotFoundException;

}

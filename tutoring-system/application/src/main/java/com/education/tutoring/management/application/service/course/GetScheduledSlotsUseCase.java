package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.in.course.GetScheduledSlots;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all scheduled class slots for a specific course.
 */
@Slf4j
@UseCase
class GetScheduledSlotsUseCase implements GetScheduledSlots {

	private final CoursePersistence coursePersistence;

	public GetScheduledSlotsUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the retrieval of scheduled slots for the given course.
	 * @param courseId the unique identifier of the course
	 * @return a list of {@link ScheduledSlotDTO} representing the course's timetable
	 * @throws ResourceNotFoundException if the course cannot be found in the system
	 */
	@Override
	public List<ScheduledSlotDTO> execute(Long courseId) throws ResourceNotFoundException {

		log.debug("Fetching scheduled slots for course ID: {}", courseId);
		return coursePersistence.getScheduledSlotsByCourseId(courseId);
	}

}

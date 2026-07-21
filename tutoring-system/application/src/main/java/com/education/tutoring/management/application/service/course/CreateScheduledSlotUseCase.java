package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.in.course.CreateScheduledSlot;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.TeacherNotAssignedToCourseException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for creating and scheduling a new class slot for a course.
 */
@Slf4j
@UseCase
class CreateScheduledSlotUseCase implements CreateScheduledSlot {

	private final CoursePersistence coursePersistence;

	public CreateScheduledSlotUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the creation of a scheduled class slot.
	 * @param courseId the ID of the course
	 * @param scheduledSlotDTO the data containing slot details (time, classroom, etc.)
	 * @return the created {@link ScheduledSlotDTO} with its generated ID
	 * @throws ResourceNotFoundException if the course or teacher is not found
	 * @throws TeacherNotAssignedToCourseException if the teacher is not authorized to
	 * teach the given course
	 * @throws IllegalOperationException if there is a scheduling conflict (teacher or
	 * classroom overlap)
	 */
	@Override
	public ScheduledSlotDTO execute(Long courseId, ScheduledSlotDTO scheduledSlotDTO)
			throws ResourceNotFoundException, TeacherNotAssignedToCourseException, IllegalOperationException {

		ScheduledSlotDTO createdSlot = coursePersistence.createScheduledSlot(courseId, scheduledSlotDTO);
		log.info("Successfully created scheduled slot ID: {} for course ID: {}", createdSlot.getId(), courseId);

		return createdSlot;
	}

}

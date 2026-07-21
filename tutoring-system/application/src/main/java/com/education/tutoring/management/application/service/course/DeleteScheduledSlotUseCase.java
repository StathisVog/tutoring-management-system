package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.port.in.course.DeleteScheduledSlot;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a scheduled class slot from a
 * course.
 */
@Slf4j
@UseCase
class DeleteScheduledSlotUseCase implements DeleteScheduledSlot {

	private final CoursePersistence coursePersistence;

	public DeleteScheduledSlotUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the deletion of a scheduled slot.
	 * @param courseId the ID of the course
	 * @param slotId the ID of the scheduled slot to delete
	 * @throws ResourceNotFoundException if the course or slot is not found
	 */
	@Override
	public void execute(Long courseId, Long slotId) throws ResourceNotFoundException {

		log.warn("Admin is permanently deleting scheduled slot ID: {} from course ID: {}", slotId, courseId);
		coursePersistence.deleteScheduledSlot(courseId, slotId);
		log.info("Scheduled slot ID: {} was successfully deleted.", slotId);
	}

}

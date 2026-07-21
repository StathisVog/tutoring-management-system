package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.port.in.course.DeleteCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation of the use case for deleting an existing course.
 */
@Slf4j
@UseCase
class DeleteCourseUseCase implements DeleteCourse {

	private final CoursePersistence coursePersistence;

	public DeleteCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the use case for permanently deleting a course.
	 * @param id the unique identifier of the course to delete
	 * @throws ResourceNotFoundException if the course cannot be found in the system
	 */
	@Override
	public void execute(Long id) throws ResourceNotFoundException {

		coursePersistence.findById(id).orElseThrow(() -> {
			log.warn("Course deletion failed: Course with ID '{}' not found", id);
			return new ResourceNotFoundException(
					String.format("Course with ID '%d' was not found. Deletion failed.", id));
		});

		log.warn("Admin is permanently deleting course ID: {}", id);
		coursePersistence.deleteById(id);
		log.info("Course with ID: {} was successfully deleted.", id);
	}

}

package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.port.in.course.UpdateCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.CourseAlreadyExistsException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively updating an existing course's details.
 */
@Slf4j
@UseCase
class UpdateCourseUseCase implements UpdateCourse {

	private final CoursePersistence coursePersistence;

	public UpdateCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the update of a course, ensuring the new title (if changed) is unique.
	 * @param id the unique identifier of the course to update
	 * @param updateData the data transfer object containing the new course details
	 * @return the updated {@link CourseDTO}
	 * @throws ResourceNotFoundException if the course cannot be found
	 * @throws CourseAlreadyExistsException if the updated title clashes with an existing
	 * course
	 */
	@Override
	public CourseDTO execute(Long id, CourseDTO updateData)
			throws ResourceNotFoundException, CourseAlreadyExistsException {

		CourseDTO existingCourse = coursePersistence.findById(id).orElseThrow(() -> {
			log.warn("Course update failed: Course with ID '{}' not found", id);
			return new ResourceNotFoundException(String.format("Course with ID '%d' was not found in the system.", id));
		});

		// Check if the title has actually been modified by the Admin.
		// If only other fields (e.g., 'active') changed and the title remains the same,
		// skip this block to prevent throwing a duplicate exception against the course
		// itself.
		if (!existingCourse.getTitle().equalsIgnoreCase(updateData.getTitle())) {

			// Since the title has changed, ensure the new title is not already taken by
			// another course.
			if (coursePersistence.existsByTitle(updateData.getTitle())) {
				log.warn("Course update failed: A course with the title '{}' already exists", updateData.getTitle());
				throw new CourseAlreadyExistsException(String
					.format("A course with the title '%s' already exists in the system.", updateData.getTitle()));
			}
		}

		existingCourse.setTitle(updateData.getTitle());
		existingCourse.setDescription(updateData.getDescription());
		existingCourse.setGradeLevel(updateData.getGradeLevel());
		existingCourse.setActive(updateData.isActive());

		CourseDTO updatedCourse = coursePersistence.save(existingCourse);
		log.info("Successfully updated course ID: {} with title '{}'", updatedCourse.getId(), updatedCourse.getTitle());

		return updatedCourse;
	}

}

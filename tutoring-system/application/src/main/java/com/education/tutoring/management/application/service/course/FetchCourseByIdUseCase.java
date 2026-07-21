package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.port.in.course.FetchCourseById;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for retrieving a specific educational course by its unique
 * identifier.
 */
@Slf4j
@UseCase
class FetchCourseByIdUseCase implements FetchCourseById {

	private final CoursePersistence coursePersistence;

	public FetchCourseByIdUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the use case for fetching a course by its ID.
	 * @param id the unique identifier of the course
	 * @return the requested {@link CourseDTO}
	 * @throws ResourceNotFoundException if the course is not found in the persistence
	 * layer
	 */
	@Override
	public CourseDTO execute(Long id) throws ResourceNotFoundException {

		return coursePersistence.findById(id).orElseThrow(() -> {
			log.warn("Fetch course failed: Course with ID '{}' not found.", id);
			return new ResourceNotFoundException(String.format("Course with ID '%d' was not found in the system.", id));
		});
	}

}

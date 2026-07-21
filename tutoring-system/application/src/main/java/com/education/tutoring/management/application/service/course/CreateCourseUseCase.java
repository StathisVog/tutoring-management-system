package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.port.in.course.CreateCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.CourseAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for creating a new course.
 */
@Slf4j
@UseCase
class CreateCourseUseCase implements CreateCourse {

	private final CoursePersistence coursePersistence;

	public CreateCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the creation of a new course, ensuring the title is unique.
	 * @param courseDTO the data transfer object containing the course details
	 * @return the created {@link CourseDTO} with its assigned ID
	 * @throws CourseAlreadyExistsException if a course with the same title already exists
	 */
	@Override
	public CourseDTO execute(CourseDTO courseDTO) throws CourseAlreadyExistsException {

		if (coursePersistence.existsByTitle(courseDTO.getTitle())) {
			log.warn("Course creation failed: A course with the title '{}' already exists", courseDTO.getTitle());
			throw new CourseAlreadyExistsException(
					String.format("A course with the title '%s' already exists in the system.", courseDTO.getTitle()));
		}

		courseDTO.setActive(true);

		CourseDTO savedCourse = coursePersistence.save(courseDTO);
		log.info("Successfully created new course '{}' with ID: {}", savedCourse.getTitle(), savedCourse.getId());

		return savedCourse;
	}

}

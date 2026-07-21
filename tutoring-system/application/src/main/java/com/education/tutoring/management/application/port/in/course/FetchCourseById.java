package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for retrieving a specific educational course by its unique identifier.
 */
public interface FetchCourseById {

	/**
	 * Executes the retrieval of a course by its ID.
	 * @param id the unique identifier of the course
	 * @return the requested {@link CourseDTO}
	 * @throws ResourceNotFoundException if the course cannot be found
	 */
	CourseDTO execute(Long id) throws ResourceNotFoundException;

}

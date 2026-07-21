package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.domain.exception.CourseAlreadyExistsException;

/**
 * Interface for administratively creating a new educational course in the system.
 */
public interface CreateCourse {

	/**
	 * Executes the creation of a new course.
	 * @param courseDTO the course data to be created
	 * @return the created course with its assigned ID
	 */
	CourseDTO execute(CourseDTO courseDTO) throws CourseAlreadyExistsException;

}

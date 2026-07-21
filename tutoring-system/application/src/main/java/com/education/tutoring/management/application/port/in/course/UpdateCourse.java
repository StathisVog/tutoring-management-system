package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.domain.exception.CourseAlreadyExistsException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively updating the details of an existing educational course.
 */
public interface UpdateCourse {

	/**
	 * Executes the update of an existing course.
	 * @param id the unique identifier of the course to update
	 * @param updateData the new data for the course
	 * @return the updated {@link CourseDTO}
	 * @throws ResourceNotFoundException if the course is not found
	 * @throws CourseAlreadyExistsException if the new title clashes with another course
	 */
	CourseDTO execute(Long id, CourseDTO updateData) throws ResourceNotFoundException, CourseAlreadyExistsException;

}

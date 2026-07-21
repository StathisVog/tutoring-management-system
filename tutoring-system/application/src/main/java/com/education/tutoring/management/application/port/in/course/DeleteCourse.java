package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Executes the permanent deletion of a course.
 */
public interface DeleteCourse {

	/**
	 * Deletes a course by its ID.
	 * @param id the unique identifier of the course to delete
	 * @throws ResourceNotFoundException if the course cannot be found in the system
	 */
	void execute(Long id) throws ResourceNotFoundException;

}

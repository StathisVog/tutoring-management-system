package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Executes the assignment of a teacher to a course.
 */
public interface AssignTeacherToCourse {

	/**
	 * Links a teacher to a course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if the course or teacher cannot be found
	 */
	void execute(Long courseId, Long teacherId) throws ResourceNotFoundException;

}

package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Executes the removal of a teacher assignment from a course.
 */
public interface UnassignTeacherFromCourse {

	/**
	 * Unlinks a teacher from a course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if the course or teacher cannot be found
	 */
	void execute(Long courseId, Long teacherId) throws ResourceNotFoundException, IllegalOperationException;

}

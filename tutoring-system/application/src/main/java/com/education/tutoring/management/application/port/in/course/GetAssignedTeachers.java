package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for retrieving all teachers officially assigned to a specific course.
 */
public interface GetAssignedTeachers {

	/**
	 * Executes the retrieval of teachers for the given course.
	 * @param courseId the ID of the course
	 * @return a list of {@link AssignedTeacherDTO}
	 * @throws ResourceNotFoundException if the course is not found
	 */
	List<AssignedTeacherDTO> execute(Long courseId) throws ResourceNotFoundException;

}

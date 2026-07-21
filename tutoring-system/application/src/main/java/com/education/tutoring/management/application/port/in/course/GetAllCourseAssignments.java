package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;

import java.util.List;

/**
 * Interface for retrieving all teacher-to-course assignments across the system.
 */
public interface GetAllCourseAssignments {

	/**
	 * Executes the retrieval of all course assignments.
	 * @return a list of {@link CourseAssignmentDTO}
	 */
	List<CourseAssignmentDTO> execute();

}

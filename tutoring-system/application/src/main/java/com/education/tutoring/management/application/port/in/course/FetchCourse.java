package com.education.tutoring.management.application.port.in.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;

import java.util.List;

/**
 * Interface for retrieving a list of courses from the system, with optional filtering
 * criteria.
 */
public interface FetchCourse {

	/**
	 * Executes the retrieval of all courses, applying optional filters.
	 * @param title optional title filter
	 * @param active optional active status filter
	 * @return a list of {@link CourseDTO}s matching the criteria
	 */
	List<CourseDTO> execute(String title, Boolean active);

}

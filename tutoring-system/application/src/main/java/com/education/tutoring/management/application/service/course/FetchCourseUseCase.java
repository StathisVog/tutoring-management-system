package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.port.in.course.FetchCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving courses with optional filtering by title and
 * active status.
 */
@Slf4j
@UseCase
class FetchCourseUseCase implements FetchCourse {

	private final CoursePersistence coursePersistence;

	public FetchCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the retrieval of all courses, applying optional filters.
	 * @param title optional title filter
	 * @param active optional active status filter
	 * @return a list of {@link CourseDTO} matching the criteria
	 */
	@Override
	public List<CourseDTO> execute(String title, Boolean active) {

		log.debug("Fetching courses with filters -> Title: {}, Active: {}", title != null ? title : "ANY",
				active != null ? active : "ANY");

		return coursePersistence.findAllCourses(title, active);
	}

}

package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;
import com.education.tutoring.management.application.port.in.course.GetAllCourseAssignments;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all teacher-to-course assignments across the
 * system.
 */
@Slf4j
@UseCase
class GetAllCourseAssignmentsUseCase implements GetAllCourseAssignments {

	private final CoursePersistence coursePersistence;

	public GetAllCourseAssignmentsUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the retrieval of all course assignments.
	 * @return a list of {@link CourseAssignmentDTO} representing the assignments
	 */
	@Override
	public List<CourseAssignmentDTO> execute() {

		log.debug("Fetching all course assignments.");
		return coursePersistence.getAllCourseAssignments();
	}

}

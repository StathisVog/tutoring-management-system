package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.port.in.course.GetAssignedTeachers;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all teachers officially assigned to a specific
 * course.
 */
@Slf4j
@UseCase
class GetAssignedTeachersUseCase implements GetAssignedTeachers {

	private final CoursePersistence coursePersistence;

	public GetAssignedTeachersUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the retrieval of teachers for the given course.
	 * @param courseId the ID of the course
	 * @return a list of {@link AssignedTeacherDTO} representing the assigned teachers
	 * @throws ResourceNotFoundException if the course is not found
	 */
	@Override
	public List<AssignedTeacherDTO> execute(Long courseId) throws ResourceNotFoundException {

		log.debug("Fetching assigned teachers for course ID: {}", courseId);
		return coursePersistence.getAssignedTeachersByCourseId(courseId);
	}

}

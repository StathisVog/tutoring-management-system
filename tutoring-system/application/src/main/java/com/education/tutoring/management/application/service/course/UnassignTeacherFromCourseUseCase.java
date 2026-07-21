package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.port.in.course.UnassignTeacherFromCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively unassigning a teacher from a course.
 */
@Slf4j
@UseCase
class UnassignTeacherFromCourseUseCase implements UnassignTeacherFromCourse {

	private final CoursePersistence coursePersistence;

	public UnassignTeacherFromCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the unassignment of a teacher from a specific course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher to unassign
	 * @throws ResourceNotFoundException if either the course or the teacher cannot be
	 * found
	 * @throws IllegalOperationException if the unassignment violates business rules
	 * (e.g., teacher has active slots)
	 */
	@Override
	public void execute(Long courseId, Long teacherId) throws ResourceNotFoundException, IllegalOperationException {

		coursePersistence.unassignTeacher(courseId, teacherId);
		log.info("Successfully unassigned teacher ID: {} from course ID: {}", teacherId, courseId);
	}

}

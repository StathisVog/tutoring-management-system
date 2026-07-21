package com.education.tutoring.management.application.service.course;

import com.education.tutoring.management.application.port.in.course.AssignTeacherToCourse;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation of the use case for assigning a teacher to a course.
 */
@Slf4j
@UseCase
class AssignTeacherToCourseUseCase implements AssignTeacherToCourse {

	private final CoursePersistence coursePersistence;

	public AssignTeacherToCourseUseCase(CoursePersistence coursePersistence) {
		this.coursePersistence = coursePersistence;
	}

	/**
	 * Executes the assignment of a teacher to a specific course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if either the course or the teacher cannot be
	 * found
	 */
	@Override
	public void execute(Long courseId, Long teacherId) throws ResourceNotFoundException {

		coursePersistence.assignTeacher(courseId, teacherId);
		log.info("Successfully assigned teacher ID: {} to course ID: {}", teacherId, courseId);
	}

}

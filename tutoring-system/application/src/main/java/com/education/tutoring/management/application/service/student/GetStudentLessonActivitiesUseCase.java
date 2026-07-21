package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.port.in.student.GetStudentLessonActivities;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving lesson activities accessible to a specific
 * student.
 */
@Slf4j
@UseCase
class GetStudentLessonActivitiesUseCase implements GetStudentLessonActivities {

	private final StudentPersistence studentPersistence;

	public GetStudentLessonActivitiesUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the retrieval of lesson activities for a student, with an optional filter
	 * for a specific slot.
	 * @param studentUsername the username of the requesting student
	 * @param slotId the optional scheduled slot ID to filter activities
	 * @return a list of {@link StudentLessonActivityDTO}
	 * @throws ResourceNotFoundException if the student or slot cannot be found
	 * @throws UnauthorizedActionException if the student is not enrolled in the specified
	 * slot
	 */
	@Override
	public List<StudentLessonActivityDTO> execute(String studentUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		log.info("Student '{}' is requesting lesson activities. Filter slotId: {}", studentUsername,
				slotId != null ? slotId : "NONE");
		return studentPersistence.getStudentLessonActivities(studentUsername, slotId);
	}

}

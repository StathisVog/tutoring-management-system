package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.LessonActivityDTO;
import com.education.tutoring.management.application.port.in.teacher.GetLessonActivities;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving lesson activities logged by a teacher, with an
 * optional filter for a specific class slot.
 */
@Slf4j
@UseCase
class GetLessonActivitiesUseCase implements GetLessonActivities {

	private final TeacherPersistence teacherPersistence;

	public GetLessonActivitiesUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of lesson activities for the authenticated teacher.
	 * @param teacherUsername the username of the teacher requesting the activities
	 * @param slotId the optional scheduled slot ID to filter activities
	 * @return a chronologically ordered list of {@link LessonActivityDTO}
	 * @throws ResourceNotFoundException if the specified slot cannot be found
	 * @throws UnauthorizedActionException if the teacher is not assigned to the slot
	 */
	@Override
	public List<LessonActivityDTO> execute(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		log.debug("Teacher '{}' fetching lesson activities for slot ID: {}", teacherUsername, slotId);
		return teacherPersistence.getLessonActivities(teacherUsername, slotId);
	}

}

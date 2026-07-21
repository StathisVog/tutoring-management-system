package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.UpdateLessonActivityDTO;
import com.education.tutoring.management.application.port.in.teacher.UpdateLessonActivity;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for allowing a teacher to update an existing lesson activity
 * they created.
 */
@Slf4j
@UseCase
class UpdateLessonActivityUseCase implements UpdateLessonActivity {

	private final TeacherPersistence teacherPersistence;

	public UpdateLessonActivityUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the update of a specific lesson activity.
	 * @param teacherUsername the username of the updating teacher
	 * @param activityId the ID of the lesson activity to update
	 * @param updateLessonActivityDTO the new data for the activity
	 * @throws ResourceNotFoundException if the activity cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the activity
	 * @throws IllegalOperationException if the time window for updating the activity has
	 * expired
	 */
	@Override
	public void execute(String teacherUsername, Long activityId, UpdateLessonActivityDTO updateLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		log.info("Teacher '{}' is executing an update on lesson activity ID: {}", teacherUsername, activityId);

		teacherPersistence.updateLessonActivity(teacherUsername, activityId, updateLessonActivityDTO);

		log.info("Successfully updated lesson activity ID: {}", activityId);
	}

}

package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.port.in.teacher.DeleteLessonActivity;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for a teacher to delete an existing lesson activity they
 * created.
 */
@Slf4j
@UseCase
class DeleteLessonActivityUseCase implements DeleteLessonActivity {

	private final TeacherPersistence teacherPersistence;

	public DeleteLessonActivityUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the deletion of a specific lesson activity.
	 * @param teacherUsername the username of the requesting teacher
	 * @param activityId the ID of the lesson activity to delete
	 * @throws ResourceNotFoundException if the activity cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the activity
	 * @throws IllegalOperationException if the deletion time window has expired
	 */
	@Override
	public void execute(String teacherUsername, Long activityId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		log.warn("Teacher '{}' is attempting to permanently delete lesson activity ID: {}", teacherUsername,
				activityId);

		teacherPersistence.deleteLessonActivity(teacherUsername, activityId);

		log.info("Lesson activity ID: {} successfully deleted by teacher '{}'", activityId, teacherUsername);
	}

}

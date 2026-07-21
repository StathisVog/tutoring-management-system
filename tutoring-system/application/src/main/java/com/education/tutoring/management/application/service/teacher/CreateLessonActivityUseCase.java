package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.CreateLessonActivityDTO;
import com.education.tutoring.management.application.port.in.teacher.CreateLessonActivity;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for a teacher to log a new lesson activity for a specific class
 * slot.
 */
@Slf4j
@UseCase
class CreateLessonActivityUseCase implements CreateLessonActivity {

	private final TeacherPersistence teacherPersistence;

	public CreateLessonActivityUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the creation of a lesson activity.
	 * @param teacherUsername the username of the requesting teacher
	 * @param slotId the ID of the scheduled slot
	 * @param createLessonActivityDTO the details of the lesson activity
	 * @throws ResourceNotFoundException if the slot cannot be found
	 * @throws UnauthorizedActionException if the teacher is not authorized for this slot
	 * @throws IllegalOperationException if the creation violates business rules
	 */
	@Override
	public void execute(String teacherUsername, Long slotId, CreateLessonActivityDTO createLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		log.info("Teacher '{}' is executing lesson activity creation for slot ID: {} on date: {}", teacherUsername,
				slotId, createLessonActivityDTO.getDate());

		teacherPersistence.createLessonActivity(teacherUsername, slotId, createLessonActivityDTO);

		log.info("Successfully created lesson activity for slot ID: {}", slotId);
	}

}

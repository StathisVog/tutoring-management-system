package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.port.in.teacher.DeleteTeacherAbsence;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for a teacher to cancel/delete a previously scheduled absence.
 */
@Slf4j
@UseCase
class DeleteTeacherAbsenceUseCase implements DeleteTeacherAbsence {

	private final TeacherPersistence teacherPersistence;

	public DeleteTeacherAbsenceUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the deletion of a teacher absence record.
	 * @param teacherUsername the username of the requesting teacher
	 * @param absenceId the ID of the absence record to delete
	 * @throws ResourceNotFoundException if the absence record cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the absence record
	 * @throws IllegalOperationException if the absence is in the past and cannot be
	 * deleted
	 */
	@Override
	public void execute(String teacherUsername, Long absenceId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		log.warn("Teacher '{}' is attempting to cancel/delete absence ID: {}", teacherUsername, absenceId);

		teacherPersistence.deleteTeacherAbsence(teacherUsername, absenceId);

		log.info("Absence ID: {} successfully deleted by teacher '{}'", absenceId, teacherUsername);
	}

}

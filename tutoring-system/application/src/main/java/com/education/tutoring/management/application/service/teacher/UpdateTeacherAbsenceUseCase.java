package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.AbsenceRequestDTO;
import com.education.tutoring.management.application.port.in.teacher.UpdateTeacherAbsence;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for allowing a teacher to update a previously scheduled
 * absence.
 */
@Slf4j
@UseCase
class UpdateTeacherAbsenceUseCase implements UpdateTeacherAbsence {

	private final TeacherPersistence teacherPersistence;

	public UpdateTeacherAbsenceUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the update of a teacher absence record.
	 * @param teacherUsername the username of the updating teacher
	 * @param absenceId the ID of the absence record to update
	 * @param absenceRequestDTO the new details for the absence
	 * @throws ResourceNotFoundException if the absence record cannot be found
	 * @throws UnauthorizedActionException if the teacher does not own the absence
	 * @throws IllegalOperationException if the absence date is in the past and cannot be
	 * modified
	 */
	@Override
	public void execute(String teacherUsername, Long absenceId, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		teacherPersistence.updateTeacherAbsence(teacherUsername, absenceId, absenceRequestDTO);

		log.info("Teacher '{}' successfully updated absence ID: {} to new date: {}", teacherUsername, absenceId,
				absenceRequestDTO.getDate());
	}

}

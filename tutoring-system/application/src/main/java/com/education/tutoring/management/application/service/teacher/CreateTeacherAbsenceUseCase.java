package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.AbsenceRequestDTO;
import com.education.tutoring.management.application.port.in.teacher.CreateTeacherAbsence;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for a teacher to declare and log a scheduled absence.
 */
@Slf4j
@UseCase
class CreateTeacherAbsenceUseCase implements CreateTeacherAbsence {

	private final TeacherPersistence teacherPersistence;

	public CreateTeacherAbsenceUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the creation of a teacher absence record.
	 * @param teacherUsername the username of the requesting teacher
	 * @param absenceRequestDTO the data containing the absence details (date, specific
	 * slot or full day)
	 * @throws ResourceNotFoundException if the specified slot (if any) cannot be found
	 * @throws IllegalOperationException if an absence already exists or violates
	 * scheduling rules
	 * @throws UnauthorizedActionException if the teacher is not authorized for the
	 * specified slot
	 */
	@Override
	public void execute(String teacherUsername, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, IllegalOperationException, UnauthorizedActionException {

		teacherPersistence.createTeacherAbsence(teacherUsername, absenceRequestDTO);
		log.info("Teacher '{}' successfully logged an absence for date: {}", teacherUsername,
				absenceRequestDTO.getDate());
	}

}

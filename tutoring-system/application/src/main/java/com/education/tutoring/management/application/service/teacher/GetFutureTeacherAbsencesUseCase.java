package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.TeacherAbsenceDTO;
import com.education.tutoring.management.application.port.in.teacher.GetFutureTeacherAbsences;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving a teacher's own future scheduled absences.
 */
@Slf4j
@UseCase
class GetFutureTeacherAbsencesUseCase implements GetFutureTeacherAbsences {

	private final TeacherPersistence teacherPersistence;

	public GetFutureTeacherAbsencesUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of future absences for the authenticated teacher.
	 * @param teacherUsername the username of the requesting teacher
	 * @return a list of {@link TeacherAbsenceDTO} representing the upcoming absences
	 * @throws ResourceNotFoundException if the teacher cannot be found
	 */
	@Override
	public List<TeacherAbsenceDTO> execute(String teacherUsername) throws ResourceNotFoundException {

		log.debug("Fetching future absences for teacher: '{}'", teacherUsername);
		return teacherPersistence.getFutureTeacherAbsences(teacherUsername);
	}

}

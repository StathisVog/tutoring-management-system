package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.ActiveStudentDTO;
import com.education.tutoring.management.application.port.in.teacher.GetActiveStudentsForSlot;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving the roster of active students for a teacher's
 * scheduled slot.
 */
@Slf4j
@UseCase
class GetActiveStudentsForSlotUseCase implements GetActiveStudentsForSlot {

	private final TeacherPersistence teacherPersistence;

	public GetActiveStudentsForSlotUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of active students enrolled in the specified slot.
	 * @param teacherUsername the username of the requesting teacher
	 * @param slotId the ID of the scheduled slot
	 * @return a list of {@link ActiveStudentDTO} representing the class roster
	 * @throws ResourceNotFoundException if the scheduled slot cannot be found
	 * @throws UnauthorizedActionException if the teacher is not assigned to the slot
	 */
	@Override
	public List<ActiveStudentDTO> execute(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		log.debug("Executing retrieval of active students for slot {} requested by teacher '{}'", slotId,
				teacherUsername);

		return teacherPersistence.getActiveStudentsForSlot(teacherUsername, slotId);
	}

}

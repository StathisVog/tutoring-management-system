package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.CreateTestRequestDTO;
import com.education.tutoring.management.application.port.in.teacher.CreateAndAssignTest;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for a teacher to create a new test and assign it to the
 * students of a specific class slot.
 */
@Slf4j
@UseCase
class CreateAndAssignTestUseCase implements CreateAndAssignTest {

	private final TeacherPersistence teacherPersistence;

	public CreateAndAssignTestUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the creation and assignment of a test.
	 * @param teacherUsername the username of the requesting teacher
	 * @param createTestRequestDTO the data transfer object containing the test details
	 * @throws ResourceNotFoundException if the scheduled slot cannot be found
	 * @throws UnauthorizedActionException if the teacher is not assigned to the specified
	 * slot
	 * @throws IllegalOperationException if the assignment violates business rules
	 */
	@Override
	public void execute(String teacherUsername, CreateTestRequestDTO createTestRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		teacherPersistence.createAndAssignTest(teacherUsername, createTestRequestDTO);
		log.info("Teacher '{}' successfully created and assigned a test for date: {}", teacherUsername,
				createTestRequestDTO.getDate());
	}

}

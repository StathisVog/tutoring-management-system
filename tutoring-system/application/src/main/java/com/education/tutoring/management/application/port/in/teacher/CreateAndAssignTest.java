package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.CreateTestRequestDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

/**
 * Interface for a teacher to create a new test and assign it to either an entire class or
 * specific active students within that class.
 */
public interface CreateAndAssignTest {

	/**
	 * Executes the creation and assignment of a test.
	 * @param teacherUsername the username of the requesting teacher
	 * @param createTestRequestDTO the details of the test to be created and assigned
	 * @throws ResourceNotFoundException if the teacher or scheduled slot is not found
	 * @throws UnauthorizedActionException if the teacher attempts to assign a test to a
	 * class they do not teach
	 * @throws IllegalOperationException if the assignment violates business rules (e.g.,
	 * wrong course, inactive students)
	 */
	void execute(String teacherUsername, CreateTestRequestDTO createTestRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

}

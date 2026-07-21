package com.education.tutoring.management.application.port.in.authentication;

import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.domain.exception.RegisterUserException;

/**
 * Interface for registering a new student in the system.
 */
public interface RegisterStudent {

	/**
	 * Executes the student registration process.
	 * @param registerStudentDTO the registration details specific to a student
	 * @throws RegisterUserException if validation fails or the username/email already
	 * exists
	 */
	void execute(RegisterStudentDTO registerStudentDTO) throws RegisterUserException;

}

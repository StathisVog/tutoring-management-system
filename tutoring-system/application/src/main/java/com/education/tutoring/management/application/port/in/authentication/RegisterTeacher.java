package com.education.tutoring.management.application.port.in.authentication;

import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.domain.exception.RegisterUserException;

/**
 * Interface for registering a new teacher in the system.
 */
public interface RegisterTeacher {

	/**
	 * Executes the teacher registration process.
	 * @param registerTeacherDTO the registration details specific to a teacher
	 * @throws RegisterUserException if validation fails or the username/email already
	 * exists
	 */
	void execute(RegisterTeacherDTO registerTeacherDTO) throws RegisterUserException;

}

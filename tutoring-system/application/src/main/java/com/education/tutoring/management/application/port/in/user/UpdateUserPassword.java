package com.education.tutoring.management.application.port.in.user;

import com.education.tutoring.management.application.dto.user.UpdatePasswordDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UpdateUserException;

/**
 * Interface for securely updating an authenticated user's password.
 */
public interface UpdateUserPassword {

	/**
	 * Updates the password of the specified user after verifying the current password.
	 * @param username the username of the user requesting the change
	 * @param command the DTO containing old, new, and confirmation passwords
	 * @throws ResourceNotFoundException if the user does not exist
	 * @throws UpdateUserException if the password business rules are violated
	 */
	void execute(String username, UpdatePasswordDTO command) throws ResourceNotFoundException, UpdateUserException;

}

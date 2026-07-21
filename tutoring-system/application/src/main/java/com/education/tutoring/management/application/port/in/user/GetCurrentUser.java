package com.education.tutoring.management.application.port.in.user;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for retrieving the currently authenticated user's details.
 */
public interface GetCurrentUser {

	/**
	 * Retrieves the profile details of the user based on their username.
	 * @param username the username extracted from the security context
	 * @return a {@link UserDTO} containing the public user data
	 * @throws ResourceNotFoundException if the user is not found in the database
	 */
	UserDTO execute(String username) throws ResourceNotFoundException;

}

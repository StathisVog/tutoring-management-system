package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for retrieving a user's details by their username.
 */
public interface GetUserByUsername {

	/**
	 * Executes the retrieval of a user based on their username.
	 * @param username the username of the user to retrieve
	 * @return a {@link UserDTO} representing the requested user
	 * @throws ResourceNotFoundException if no user is found with the provided username
	 */
	UserDTO execute(String username) throws ResourceNotFoundException;

}

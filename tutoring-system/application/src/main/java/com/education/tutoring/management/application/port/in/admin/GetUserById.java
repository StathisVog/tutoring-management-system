package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for retrieving a user's details by their unique identifier.
 */
public interface GetUserById {

	/**
	 * Executes the retrieval of a user based on their ID.
	 * @param id the unique identifier of the user to retrieve
	 * @return a {@link UserDTO} representing the requested user
	 * @throws ResourceNotFoundException if no user is found with the provided ID
	 */
	UserDTO execute(Long id) throws ResourceNotFoundException;

}

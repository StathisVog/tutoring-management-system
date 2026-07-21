package com.education.tutoring.management.application.port.in.user;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UpdateUserException;

/**
 * Interface for allowing an authenticated user to update their own profile details.
 */
public interface UpdateCurrentUserProfile {

	/**
	 * Updates specific profile fields for the given user.
	 * @param username the current user's username
	 * @param updatedData the incoming DTO with new data
	 * @return the updated UserDTO reflecting the changes
	 * @throws ResourceNotFoundException if the user cannot be found in the system
	 * @throws UpdateUserException if the updated data violates domain rules or
	 * constraints
	 */
	UserDTO execute(String username, UserDTO updatedData) throws ResourceNotFoundException, UpdateUserException;

}

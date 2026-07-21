package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively activating a user account.
 */
public interface ActivateUser {

	/**
	 * Activates a user account.
	 * @param userId the unique identifier of the user
	 * @throws ResourceNotFoundException if the user does not exist
	 */
	void execute(Long userId) throws ResourceNotFoundException;

}

package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

/**
 * Interface for administratively deleting a user from the system.
 */
public interface DeleteUser {

	/**
	 * Executes the use case for permanently deleting a user account.
	 * @param idToDelete the unique identifier of the user to be deleted
	 * @param currentAdminUsername the username of the admin performing the action to
	 * prevent self-deletion
	 * @throws ResourceNotFoundException if the user to delete cannot be found in the
	 * system
	 * @throws IllegalOperationException if the operation is not permitted (e.g., an admin
	 * attempting to delete their own account)
	 */
	void execute(Long idToDelete, String currentAdminUsername)
			throws ResourceNotFoundException, IllegalOperationException;

}

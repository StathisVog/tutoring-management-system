package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.admin.DeleteUser;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a user account from the system.
 */
@Slf4j
@UseCase
class DeleteUserUseCase implements DeleteUser {

	private final UserPersistence userPersistence;

	public DeleteUserUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the use case for deleting a user account, ensuring an admin cannot delete
	 * their own account.
	 * @param idToDelete the unique identifier of the user to be deleted
	 * @param currentAdminUsername the username of the admin performing the action
	 * @throws ResourceNotFoundException if either the current admin or the target user
	 * cannot be found
	 * @throws IllegalOperationException if the admin attempts to delete their own account
	 */
	@Override
	public void execute(Long idToDelete, String currentAdminUsername)
			throws ResourceNotFoundException, IllegalOperationException {

		UserDTO admin = userPersistence.findByUsername(currentAdminUsername)
			.orElseThrow(() -> new ResourceNotFoundException("Current admin not found."));

		if (admin.getId().equals(idToDelete)) {
			log.error("Security violation: Admin '{}' tried to delete their own account.", currentAdminUsername);
			throw new IllegalOperationException("You cannot delete your own administrative account.");
		}

		userPersistence.findById(idToDelete)
			.orElseThrow(() -> new ResourceNotFoundException("User with ID '" + idToDelete + "' not found."));

		userPersistence.deleteById(idToDelete);
		log.info("User with ID {} was successfully deleted by Admin.", idToDelete);
	}

}

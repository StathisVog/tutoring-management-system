package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.ActivateUser;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively activating a user account.
 */
@Slf4j
@UseCase
class ActivateUserUseCase implements ActivateUser {

	private final UserPersistence userPersistence;

	public ActivateUserUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the activation process for a specific user.
	 * @param userId the unique identifier of the user to activate
	 * @throws ResourceNotFoundException if the user cannot be found in the system
	 */
	@Override
	public void execute(Long userId) throws ResourceNotFoundException {

		userPersistence.findById(userId).orElseThrow(() -> {
			log.warn("User activation failed: User with ID {} not found", userId);
			return new ResourceNotFoundException("User with ID " + userId + " not found");
		});

		userPersistence.activateUser(userId);
		log.info("Successfully activated user with ID: {}", userId);
	}

}

package com.education.tutoring.management.application.service.user;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.user.GetCurrentUser;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation responsible for fetching the current user's profile data.
 */
@Slf4j
@UseCase
class GetCurrentUserUseCase implements GetCurrentUser {

	private final UserPersistence userPersistence;

	public GetCurrentUserUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the retrieval of the authenticated user's profile.
	 * @param username the username of the currently logged-in user
	 * @return the {@link UserDTO} representing the user's profile
	 * @throws ResourceNotFoundException if the user cannot be found in the database
	 */
	@Override
	public UserDTO execute(String username) throws ResourceNotFoundException {

		return userPersistence.findByUsername(username).orElseThrow(() -> {
			log.warn("Fetch current user failed: User with username '{}' not found.", username);
			return new ResourceNotFoundException("User with username '" + username + "' not found.");
		});
	}

}

package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.admin.GetUserByUsername;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for retrieving a specific user's details by their username.
 */
@Slf4j
@UseCase
class GetUserByUsernameUseCase implements GetUserByUsername {

	private final UserPersistence userPersistence;

	public GetUserByUsernameUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the retrieval of a user based on their username.
	 * @param username the username of the user to retrieve
	 * @return the requested {@link UserDTO}
	 * @throws ResourceNotFoundException if no user is found with the provided username
	 */
	@Override
	public UserDTO execute(String username) throws ResourceNotFoundException {
		return userPersistence.findByUsername(username).orElseThrow(() -> {
			log.warn("Fetch user failed: User with username '{}' not found.", username);
			return new ResourceNotFoundException("User with username '" + username + "' not found.");
		});
	}

}

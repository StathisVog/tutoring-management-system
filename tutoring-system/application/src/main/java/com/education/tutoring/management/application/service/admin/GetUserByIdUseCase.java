package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.admin.GetUserById;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for retrieving a specific user's details by their unique
 * identifier.
 */
@Slf4j
@UseCase
class GetUserByIdUseCase implements GetUserById {

	private final UserPersistence userPersistence;

	public GetUserByIdUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the retrieval of a user based on their ID.
	 * @param id the unique identifier of the user to retrieve
	 * @return the requested {@link UserDTO}
	 * @throws ResourceNotFoundException if no user is found with the provided ID
	 */
	@Override
	public UserDTO execute(Long id) throws ResourceNotFoundException {
		return userPersistence.findById(id).orElseThrow(() -> {
			log.warn("Fetch user failed: User with ID '{}' not found.", id);
			return new ResourceNotFoundException("User with ID '" + id + "' not found.");
		});
	}

}

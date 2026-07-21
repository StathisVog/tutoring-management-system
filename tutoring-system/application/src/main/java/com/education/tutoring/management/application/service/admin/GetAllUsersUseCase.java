package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.admin.GetAllUsers;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.util.Role;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving all system users, with an optional role filter.
 */
@Slf4j
@UseCase
class GetAllUsersUseCase implements GetAllUsers {

	private final UserPersistence userPersistence;

	public GetAllUsersUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the retrieval of users from the system, applying a role filter if
	 * provided.
	 * @param role the optional {@link Role} to filter the users by (e.g., STUDENT,
	 * TEACHER, ADMIN)
	 * @return a list of {@link UserDTO} representing the requested users
	 */
	@Override
	public List<UserDTO> execute(Role role) {

		log.info("Admin requested to fetch all users");
		return userPersistence.findAllUsers(role);

	}

}

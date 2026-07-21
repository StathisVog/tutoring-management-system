package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.util.Role;

import java.util.List;

/**
 * Interface for retrieving all users from the system, optionally filtered by role.
 */
public interface GetAllUsers {

	/**
	 * Executes the retrieval of users based on the specified role.
	 * @param role the user role to filter by (e.g., STUDENT, TEACHER), or null to
	 * retrieve all users
	 * @return a list of {@link UserDTO} objects representing the requested users
	 */
	List<UserDTO> execute(Role role);

}

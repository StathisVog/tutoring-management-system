package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.util.Role;

import java.util.List;
import java.util.Optional;

/**
 * Persistence interface for user-related operations.
 */
public interface UserPersistence {

	/**
	 * Checks if the given username already exists in the system.
	 * @param username the username to check
	 * @return true if the username is already taken, false otherwise
	 */
	boolean existsByUsername(String username);

	/**
	 * Checks if the given email already exists in the system.
	 * @param email the email address to check
	 * @return true if the email is already registered, false otherwise
	 */
	boolean existsByEmail(String email);

	/**
	 * Retrieves a user based on their unique username.
	 * @param username the username of the user to retrieve
	 * @return an {@link Optional} containing the {@link UserDTO} if found, or an empty
	 * Optional if not found
	 */
	Optional<UserDTO> findByUsername(String username);

	/**
	 * Retrieves a user based on their unique database identifier.
	 * @param id the unique identifier of the user
	 * @return an {@link Optional} containing the {@link UserDTO} if found, or an empty
	 * Optional if not found
	 */
	Optional<UserDTO> findById(Long id);

	/**
	 * Retrieves a user based on their email address.
	 * @param email the email address to search for
	 * @return an {@link Optional} containing the {@link UserDTO} if found, or empty if
	 * not found
	 */
	Optional<UserDTO> findByEmail(String email);

	/**
	 * Activates a user account, allowing them to authenticate and access the system.
	 * @param id the unique identifier of the user to activate
	 */
	void activateUser(Long id);

	/**
	 * Retrieves users from the system, optionally filtering them by their role. If no
	 * role is specified, it returns a comprehensive list of all users.
	 * @param role the specific {@link Role} to filter by, or null to retrieve all users
	 * @return a list of polymorphic {@link UserDTO} representing the fetched users
	 */
	List<UserDTO> findAllUsers(Role role);

	/**
	 * Deletes a user from the system by their unique identifier.
	 * @param id the unique identifier of the user to delete
	 */
	void deleteById(Long id);

	/**
	 * Selectively updates the user's profile based on their polymorphic type.
	 * @param username the username of the user to update
	 * @param updatedData the DTO containing the fields to be updated
	 * @return the updated {@link UserDTO}
	 * @throws ResourceNotFoundException if the user does not exist in the database
	 */
	UserDTO updateProfile(String username, UserDTO updatedData) throws ResourceNotFoundException;

	/**
	 * Updates the password hash for the specified user.
	 * @param username the username of the user
	 * @param newPasswordHash the new encrypted password hash
	 * @throws ResourceNotFoundException if the user does not exist in the database
	 */
	void updatePasswordHash(String username, String newPasswordHash) throws ResourceNotFoundException;

}

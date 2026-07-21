package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.domain.util.Role;
import com.education.tutoring.management.repository.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing base {@link User} entities. Provides core data access
 * operations, existence checks, and unique credential lookups for all system users
 * regardless of their specific role.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Checks if a user exists by username.
	 * @param username the username
	 * @return true if exists, false otherwise
	 */
	boolean existsByUsername(String username);

	/**
	 * Checks if a user exists by email.
	 * @param email the email
	 * @return true if exists, false otherwise
	 */
	boolean existsByEmail(String email);

	/**
	 * Retrieves a user entity based on their unique username.
	 * @param username the username of the user to retrieve
	 * @return an {@link Optional} containing the {@link User} if found, or an empty
	 * Optional otherwise
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Searches for a user entity by its email address.
	 * @param email the exact email address to query
	 * @return an {@link Optional} containing the {@link User} entity if a match is found
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Retrieves a list of users matching the specified role.
	 * @param role the {@link Role} to search for
	 * @return a list of {@link User} entities that possess the given role
	 */
	List<User> findAllByRole(Role role);

	/**
	 * Activates a user account by setting their enabled status to true directly in the
	 * database.
	 * @param id the unique identifier of the user to be activated
	 */
	@Modifying
	@Query("UPDATE User u SET u.enabled = true WHERE u.id = :id")
	void activateUser(@Param("id") Long id);

}

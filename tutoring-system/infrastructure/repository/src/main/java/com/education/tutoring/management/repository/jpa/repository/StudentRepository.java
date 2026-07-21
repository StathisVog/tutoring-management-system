package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Student} entities. Provides core data access
 * operations, including specialized lookups by unique credentials (username, email) and
 * active status.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	/**
	 * Retrieves a student by their unique username.
	 * @param username the unique username of the student
	 * @return an {@link Optional} containing the {@link Student} if found, or empty
	 * otherwise
	 */
	Optional<Student> findByUsername(String username);

	/**
	 * Finds students by their full name.
	 * @param fullName the full name of the student
	 * @return a list of students matching the given name
	 */
	List<Student> findByFullName(String fullName);

	/**
	 * Retrieves a list of students based on their active status in the system.
	 * @param enabled the status flag to filter by (true for active, false for inactive)
	 * @return a list of {@link Student} entities matching the specified active status
	 */
	List<Student> findByEnabled(boolean enabled);

	/**
	 * Retrieves a student by their unique email address.
	 * @param email the exact email address to search for
	 * @return an {@link Optional} containing the {@link Student} if found, or empty
	 * otherwise
	 */
	Optional<Student> findByEmail(String email);

}

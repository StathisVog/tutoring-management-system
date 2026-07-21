package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link Test} entity data access operations. Extends
 * {@link JpaSpecificationExecutor} to enable dynamic filtering and deep fetch joins
 * across tests, courses, and student results.
 */
@Repository
public interface TestRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {

	/**
	 * Retrieves all tests authored by a specific teacher within a given date range,
	 * ordered by the test date in descending order (newest first).
	 * @param username the unique username of the teacher
	 * @param startDate the start date of the range (inclusive)
	 * @param endDate the end date of the range (inclusive)
	 * @return a list of {@link Test} entities matching the criteria
	 */
	List<Test> findAllByAuthorUsernameAndDateBetweenOrderByDateDesc(String username, LocalDate startDate,
			LocalDate endDate);

	/**
	 * Checks if a test already exists for a specific course on a specific date, EXCLUDING
	 * a specific test ID. Used during updates to prevent date collisions without falsely
	 * flagging the test currently being updated.
	 * @param courseId the ID of the course
	 * @param date the new date to check
	 * @param testId the ID of the test being updated (to exclude from the check)
	 * @return true if a collision exists, false otherwise
	 */
	boolean existsByCourseIdAndDateAndIdNot(Long courseId, LocalDate date, Long testId);

	/**
	 * Checks if a test already exists for a specific course on a specific date. Used
	 * during test creation to prevent unique constraint violations.
	 */
	boolean existsByCourseIdAndDate(Long courseId, LocalDate date);

}

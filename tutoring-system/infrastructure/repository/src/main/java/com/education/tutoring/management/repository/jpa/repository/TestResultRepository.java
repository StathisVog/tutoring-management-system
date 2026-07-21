package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link TestResult} entities. Provides optimized data
 * access operations using custom fetch joins to retrieve student grades and complete test
 * histories without N+1 issues.
 */
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

	/**
	 * Retrieves all test results for a specific test, eagerly fetching the associated
	 * students.
	 * @param testId the ID of the test
	 * @return a list of {@link TestResult} entities with initialized students
	 */
	@Query("SELECT tr FROM TestResult tr JOIN FETCH tr.student WHERE tr.test.id = :testId")
	List<TestResult> findAllByTestIdWithStudent(@Param("testId") Long testId);

	/**
	 * Retrieves all test results for a specific student identified by their username. The
	 * results are ordered by the test date in descending order (newest first).
	 * @param username the username of the student
	 * @return a list of {@link TestResult} entities containing the student's test history
	 */
	@Query("SELECT tr FROM TestResult tr JOIN FETCH tr.test t JOIN FETCH t.course c "
			+ "JOIN FETCH t.author a WHERE tr.student.username = :username ORDER BY t.date DESC")
	List<TestResult> findAllByStudentUsernameWithDetailsOrderByDateDesc(@Param("username") String username);

}

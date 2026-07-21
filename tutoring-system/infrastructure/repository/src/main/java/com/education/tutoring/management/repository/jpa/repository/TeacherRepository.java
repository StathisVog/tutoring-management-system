package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Teacher} entities. Provides advanced data
 * access operations, including optimized fetch joins for efficiently retrieving teachers
 * along with their assigned courses.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	/**
	 * Retrieves all teachers along with their assigned courses in a single query
	 * @return a list of {@link Teacher} entities with initialized courses
	 */
	@Query("SELECT t FROM Teacher t JOIN FETCH t.courses")
	List<Teacher> findAllWithCourses();

	/**
	 * Retrieves a list of teachers who are assigned to a specific course.
	 * @param courseId the ID of the course
	 * @return a list of {@link Teacher} entities
	 */
	List<Teacher> findAllByCoursesId(Long courseId);

	/**
	 * Retrieves a teacher by their unique username.
	 * @param username the username of the teacher to search for
	 * @return an {@link Optional} containing the {@link Teacher} if found, or empty
	 * otherwise
	 */
	Optional<Teacher> findByUsername(String username);

}

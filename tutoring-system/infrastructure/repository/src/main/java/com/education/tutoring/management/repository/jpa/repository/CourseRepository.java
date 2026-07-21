package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Course} entities. Provides data access
 * operations including dynamic filtering and eager fetching via EntityGraphs.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	boolean existsByTitle(String title);

	/**
	 * Fetches courses dynamically based on optional title and active status filters. If a
	 * parameter is null, that specific filter is ignored. Uses EntityGraph to prevent the
	 * N+1 query problem by eagerly fetching assigned teachers.
	 * @param title the optional title to search for (partial match)
	 * @param active the optional active status
	 * @return a list of matching courses
	 */
	@EntityGraph(attributePaths = { "teachers" })
	@Query("SELECT c FROM Course c WHERE "
			+ "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
			+ "(:active IS NULL OR c.active = :active)")
	List<Course> findCoursesWithFilters(@Param("title") String title, @Param("active") Boolean active);

}

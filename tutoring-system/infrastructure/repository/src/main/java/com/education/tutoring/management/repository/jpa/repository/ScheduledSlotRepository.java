package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.ScheduledSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link ScheduledSlot} entities. Provides data access
 * operations for course timetables, scheduling rules, and teacher/classroom assignments.
 */
@Repository
public interface ScheduledSlotRepository extends JpaRepository<ScheduledSlot, Long> {

	/**
	 * Retrieves all scheduled slots for a specific course, ordered by day of the week and
	 * start time.
	 * @param courseId the ID of the course
	 * @return a list of {@link ScheduledSlot} entities
	 */
	List<ScheduledSlot> findAllByCourseIdOrderByDayOfWeekAscStartTimeAsc(Long courseId);

	/**
	 * Retrieves all scheduled slots in the database, ordered chronologically by day of
	 * the week and start time.
	 * @return a list of {@link ScheduledSlot} entities
	 */
	List<ScheduledSlot> findAllByOrderByDayOfWeekAscStartTimeAsc();

	/**
	 * Retrieves a scheduled slot by its ID and its associated course ID.
	 * @param id the ID of the scheduled slot
	 * @param courseId the ID of the course
	 * @return an {@link Optional} containing the scheduled slot if found, or empty
	 * otherwise
	 */
	Optional<ScheduledSlot> findByIdAndCourseId(Long id, Long courseId);

	/**
	 * Checks if there is any scheduled slot for a specific course and teacher.
	 * @param courseId the ID of the course
	 * @param teacherId the ID of the teacher
	 * @return {@code true} if at least one scheduled slot exists, {@code false} otherwise
	 */
	boolean existsByCourseIdAndTeacherId(Long courseId, Long teacherId);

	/**
	 * Retrieves all scheduled slots for the active enrollments of a specific student.
	 * Uses JOIN FETCH to prevent N+1 queries when accessing Course and Teacher details.
	 */
	@Query("SELECT s FROM ScheduledSlot s JOIN FETCH s.course c JOIN FETCH s.teacher t "
			+ "JOIN s.enrollments e WHERE e.student.username = :studentUsername AND e.status = 'ACTIVE'")
	List<ScheduledSlot> findAllActiveSlotsForStudent(@Param("studentUsername") String studentUsername);

	/**
	 * Checks if a teacher has any scheduled classes on a specific day of the week.
	 * @param teacherId the ID of the teacher
	 * @param dayOfWeek the day of the week to check (e.g., MONDAY)
	 * @return true if the teacher has at least one class on this day, false otherwise
	 */
	boolean existsByTeacherIdAndDayOfWeek(Long teacherId, DayOfWeek dayOfWeek);

	/**
	 * Checks if a specific classroom is already booked during a requested time frame on a
	 * specific day. Uses the time-overlap formula:
	 * {@code (NewStart < ExistingEnd) AND (NewEnd > ExistingStart)}. Uses UPPER and
	 * REPLACE to normalize the classroom string.
	 */
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScheduledSlot s "
			+ "WHERE UPPER(REPLACE(s.classroom, ' ', '')) = :normalizedClassroom AND s.dayOfWeek = :dayOfWeek "
			+ "AND s.startTime < :endTime AND s.endTime > :startTime")
	boolean existsOverlappingSlotInClassroom(@Param("normalizedClassroom") String normalizedClassroom,
			@Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime);

	/**
	 * Checks if a teacher is already scheduled to teach another class during a requested
	 * time frame.
	 */
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScheduledSlot s "
			+ "WHERE s.teacher.id = :teacherId AND s.dayOfWeek = :dayOfWeek AND s.startTime < :endTime "
			+ "AND s.endTime > :startTime")
	boolean existsOverlappingSlotForTeacher(@Param("teacherId") Long teacherId, @Param("dayOfWeek") DayOfWeek dayOfWeek,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

	/**
	 * Retrieves all scheduled slots assigned to a specific teacher.
	 * @param teacherId the ID of the teacher
	 * @return a list of {@link ScheduledSlot} entities
	 */
	@Query("SELECT s FROM ScheduledSlot s JOIN FETCH s.course WHERE s.teacher.id = :teacherId")
	List<ScheduledSlot> findAllByTeacherIdWithCourse(@Param("teacherId") Long teacherId);

}

package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.repository.jpa.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository interface for managing student {@link Enrollment} entities. Handles data
 * access operations related to student class registrations and their lifecycle statuses.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	/**
	 * Retrieves all enrollments for a specific student identified by their username.
	 * @param username the username of the student
	 * @return a list of {@link Enrollment} entities
	 */
	List<Enrollment> findAllByStudentUsernameOrderByEnrollmentDateDesc(String username);

	/**
	 * Retrieves enrollments for a specific student, filtered by a collection of statuses.
	 * @param username the username of the student
	 * @param statuses the collection of statuses to filter by
	 * @return a list of matching {@link Enrollment} entities
	 */
	List<Enrollment> findAllByStudentUsernameAndStatusInOrderByEnrollmentDateDesc(String username,
			Collection<EnrollmentStatus> statuses);

	/**
	 * Retrieves a list of enrollments based on their current status.
	 * @param status the status to filter by (e.g., PENDING_DROP)
	 * @return a list of {@link Enrollment} entities matching the status
	 */
	List<Enrollment> findAllByStatus(EnrollmentStatus status);

	/**
	 * Counts the number of occupied seats for a specific scheduled slot by checking for
	 * specific statuses.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param statuses the collection of statuses that occupy a seat (e.g., ACTIVE,
	 * PENDING_DROP)
	 * @return the total number of occupied seats
	 */
	int countByScheduledSlotIdAndStatusIn(Long scheduledSlotId, Collection<EnrollmentStatus> statuses);

	/**
	 * Checks if a student is already enrolled in a specific slot, excluding a specific
	 * status.
	 * @param studentId the ID of the student
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param status the status to exclude from the check (e.g., DROPPED)
	 * @return {@code true} if an active or pending enrollment exists, {@code false}
	 * otherwise
	 */
	boolean existsByStudentIdAndScheduledSlotIdAndStatusNot(Long studentId, Long scheduledSlotId,
			EnrollmentStatus status);

	/**
	 * Checks if a student has an enrollment in a specific slot with an EXACT status. Used
	 * for strict security checks (e.g., verifying ACTIVE status before revealing course
	 * material).
	 * @param studentId the ID of the student
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param status the exact status required (e.g., ACTIVE)
	 * @return {@code true} if the enrollment exactly matches the requested status,
	 * {@code false} otherwise
	 */
	boolean existsByStudentIdAndScheduledSlotIdAndStatus(Long studentId, Long scheduledSlotId, EnrollmentStatus status);

	/**
	 * Retrieves all enrollments for a specific scheduled slot matching a specific status.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param status the enrollment status to filter by (e.g., ACTIVE)
	 * @return a list of matching {@link Enrollment} entities
	 */
	List<Enrollment> findAllByScheduledSlotIdAndStatus(Long scheduledSlotId, EnrollmentStatus status);

	/**
	 * Checks if a student has an active or pending enrollment in a class that overlaps in
	 * time with a requested time frame on the same day. Uses the time-overlap formula:
	 * (NewStart < ExistingEnd) AND (NewEnd > ExistingStart).
	 * @param studentId the ID of the student
	 * @param dayOfWeek the day of the week to check
	 * @param startTime the start time of the new slot
	 * @param endTime the end time of the new slot
	 * @param droppedStatus the status to exclude (e.g., DROPPED)
	 * @return true if a time conflict exists, false otherwise
	 */
	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e JOIN e.scheduledSlot s "
			+ "WHERE e.student.id = :studentId AND e.status <> :droppedStatus AND s.dayOfWeek = :dayOfWeek "
			+ "AND s.startTime < :endTime AND s.endTime > :startTime")
	boolean existsOverlappingEnrollmentForStudent(@Param("studentId") Long studentId,
			@Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime, @Param("droppedStatus") EnrollmentStatus droppedStatus);

	/**
	 * Retrieves the count of enrolled students for all scheduled slots belonging to a
	 * specific teacher.
	 * @param teacherId the ID of the teacher
	 * @param statuses the collection of considered statuses (e.g., ACTIVE)
	 * @return a list of Object arrays where index 0 is the slotId (Long) and index 1 is
	 * the count (Long)
	 */
	@Query("SELECT e.scheduledSlot.id, COUNT(e) FROM Enrollment e WHERE e.scheduledSlot.teacher.id = :teacherId "
			+ "AND e.status IN :statuses GROUP BY e.scheduledSlot.id")
	List<Object[]> countEnrolledStudentsPerSlotForTeacher(@Param("teacherId") Long teacherId,
			@Param("statuses") Collection<EnrollmentStatus> statuses);

	/**
	 * Retrieves all enrollments for a specific scheduled slot, eagerly fetching the
	 * associated student details. Filters out enrollments matching a specific status
	 * (e.g., DROPPED) to build an accurate class roster.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param excludedStatus the status to completely exclude from the results
	 * @return a list of matching {@link Enrollment} entities
	 */
	@Query("SELECT e FROM Enrollment e JOIN FETCH e.student s WHERE e.scheduledSlot.id = :scheduledSlotId "
			+ "AND e.status != :excludedStatus ORDER BY s.fullName ASC")
	List<Enrollment> findAllByScheduledSlotIdAndStatusNotWithStudentOrderByStudentName(
			@Param("scheduledSlotId") Long scheduledSlotId, @Param("excludedStatus") EnrollmentStatus excludedStatus);

}

package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.LessonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link LessonActivity} entity data access operations. Extends
 * {@link JpaSpecificationExecutor} to support dynamic, criteria-based filtering for
 * administrative views.
 */
@Repository
public interface LessonActivityRepository
		extends JpaRepository<LessonActivity, Long>, JpaSpecificationExecutor<LessonActivity> {

	/**
	 * Checks if a lesson activity already exists for a specific scheduled slot on a given
	 * date. This prevents duplicate journal entries for the same class on the same day.
	 * @param scheduledSlotId the unique identifier of the scheduled slot
	 * @param date the date of the lesson execution
	 * @return true if an activity already exists, false otherwise
	 */
	boolean existsByScheduledSlotIdAndDate(Long scheduledSlotId, LocalDate date);

	/**
	 * Retrieves all lesson activities for a specific scheduled slot, eagerly fetching
	 * slot and course details. Ordered by date descending.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @return a list of {@link LessonActivity} entities
	 */
	@Query("SELECT la FROM LessonActivity la JOIN FETCH la.scheduledSlot s JOIN FETCH s.course c WHERE s.id = :scheduledSlotId ORDER BY la.date DESC")
	List<LessonActivity> findAllByScheduledSlotIdWithDetailsOrderByDateDesc(
			@Param("scheduledSlotId") Long scheduledSlotId);

	/**
	 * Retrieves ALL lesson activities across all scheduled slots taught by a specific
	 * teacher. Eagerly fetches slot and course details. Ordered by date descending.
	 * @param teacherId the ID of the teacher
	 * @return a list of {@link LessonActivity} entities
	 */
	@Query("SELECT la FROM LessonActivity la JOIN FETCH la.scheduledSlot s JOIN FETCH s.course c WHERE s.teacher.id = :teacherId ORDER BY la.date DESC")
	List<LessonActivity> findAllByTeacherIdWithDetailsOrderByDateDesc(@Param("teacherId") Long teacherId);

	/**
	 * Checks if a lesson activity already exists for a specific scheduled slot on a given
	 * date, excluding a specific activity ID. Used during updates to prevent duplicate
	 * conflicts.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @param date the target date to check
	 * @param id the ID of the current activity being updated to exclude from the check
	 * @return true if another activity exists for that date, false otherwise
	 */
	boolean existsByScheduledSlotIdAndDateAndIdNot(Long scheduledSlotId, LocalDate date, Long id);

	/**
	 * Retrieves lesson activities for a given list of slot IDs within a specific date
	 * range. Used to efficiently fetch activities for a weekly/monthly schedule.
	 * @param slotIds the list of scheduled slot IDs the student is enrolled in
	 * @param startDate the start date of the schedule
	 * @param endDate the end date of the schedule
	 * @return a list of matching {@link LessonActivity} entities
	 */
	@Query("SELECT la FROM LessonActivity la WHERE la.scheduledSlot.id IN :slotIds "
			+ "AND la.date >= :startDate AND la.date <= :endDate")
	List<LessonActivity> findActivitiesForSlotsInDateRange(@Param("slotIds") List<Long> slotIds,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	/**
	 * Retrieves upcoming lesson activities across all slots where the student has an
	 * ACTIVE enrollment. Eagerly fetches scheduled slot, course, and teacher details.
	 * @param studentId the ID of the student
	 * @param today the current date to filter out past activities
	 * @return a list of {@link LessonActivity} entities ordered by date ascending
	 * (nearest first)
	 */
	@Query("SELECT la FROM LessonActivity la JOIN FETCH la.scheduledSlot s JOIN FETCH s.course c "
			+ "JOIN FETCH s.teacher t WHERE la.date >= :today AND EXISTS (SELECT e FROM Enrollment e "
			+ "WHERE e.scheduledSlot.id = s.id AND e.student.id = :studentId "
			+ "AND e.status = com.education.tutoring.management.domain.util.EnrollmentStatus.ACTIVE) "
			+ "ORDER BY la.date ASC")
	List<LessonActivity> findUpcomingByStudentActiveEnrollments(@Param("studentId") Long studentId,
			@Param("today") LocalDate today);

	/**
	 * Retrieves upcoming lesson activities for a specific slot, provided the student has
	 * an ACTIVE enrollment.
	 * @param studentId the ID of the student
	 * @param slotId the ID of the specific scheduled slot
	 * @param today the current date
	 * @return a list of {@link LessonActivity} entities ordered by date ascending
	 */
	@Query("SELECT la FROM LessonActivity la JOIN FETCH la.scheduledSlot s JOIN FETCH s.course c "
			+ "JOIN FETCH s.teacher t WHERE s.id = :slotId AND la.date >= :today AND EXISTS ("
			+ "SELECT e FROM Enrollment e WHERE e.scheduledSlot.id = s.id AND e.student.id = :studentId "
			+ "AND e.status = com.education.tutoring.management.domain.util.EnrollmentStatus.ACTIVE) "
			+ "ORDER BY la.date ASC")
	List<LessonActivity> findUpcomingByStudentActiveEnrollmentAndSlotId(@Param("studentId") Long studentId,
			@Param("slotId") Long slotId, @Param("today") LocalDate today);

}

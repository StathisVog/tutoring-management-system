package com.education.tutoring.management.repository.jpa.repository;

import com.education.tutoring.management.repository.jpa.entity.TeacherAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link TeacherAbsence} entity data access operations. Extends
 * {@link JpaSpecificationExecutor} to support dynamic, criteria-based filtering for
 * administrative and personal teacher views.
 */
@Repository
public interface TeacherAbsenceRepository
		extends JpaRepository<TeacherAbsence, Long>, JpaSpecificationExecutor<TeacherAbsence> {

	/**
	 * Retrieves all absences for a specific teacher from a given date onwards, ordered
	 * chronologically (closest first).
	 * @param username the username of the teacher
	 * @param date the starting date (usually today)
	 * @return a list of matching {@link TeacherAbsence} entities
	 */
	List<TeacherAbsence> findAllByTeacherUsernameAndDateGreaterThanEqualOrderByDateAsc(String username, LocalDate date);

	/**
	 * Retrieves all teacher absences that fall within a specific date range.
	 * @param startDate the inclusive lower bound of the date range
	 * @param endDate the inclusive upper bound of the date range
	 * @return a list of {@link TeacherAbsence} records falling within the specified
	 * period
	 */
	List<TeacherAbsence> findByDateBetween(LocalDate startDate, LocalDate endDate);

	/**
	 * Checks if a teacher has already declared an absence for a specific scheduled slot
	 * on a given date. This supports the slot-specific (hybrid) cancellation logic,
	 * preventing duplicate absences for the exact same lesson on the exact same day.
	 * @param teacherId the unique identifier of the teacher
	 * @param date the date of the potential absence
	 * @param scheduledSlotId the unique identifier of the specific scheduled slot
	 * @return true if an absence for this specific slot and date already exists, false
	 * otherwise
	 */
	boolean existsByTeacherIdAndDateAndScheduledSlotId(Long teacherId, LocalDate date, Long scheduledSlotId);

	/**
	 * Checks if a teacher has already declared a full-day absence for a given date. A
	 * full-day absence is identified by the scheduledSlot field being completely null.
	 * This prevents a teacher from declaring multiple full-day absences for the exact
	 * same date.
	 * @param teacherId the unique identifier of the teacher
	 * @param date the date of the potential full-day absence
	 * @return true if a full-day absence (null slot) already exists for this date, false
	 * otherwise
	 */
	boolean existsByTeacherIdAndDateAndScheduledSlotIsNull(Long teacherId, LocalDate date);

	/**
	 * Retrieves all absences for a specific teacher that fall within a specified date
	 * range. Dynamically checking schedule cancellations over a given period.
	 * @param teacherId the ID of the teacher
	 * @param startDate the start date of the boundary
	 * @param endDate the end date of the boundary
	 * @return a list of matching {@link TeacherAbsence} entities
	 */
	List<TeacherAbsence> findAllByTeacherIdAndDateBetween(Long teacherId, LocalDate startDate, LocalDate endDate);

	/**
	 * Checks if a slot-specific absence already exists, excluding a specific absence ID.
	 * Used for Admin updates to prevent collisions while allowing the current record to
	 * be updated.
	 * @param teacherId the ID of the teacher
	 * @param date the date of the teacher absence
	 * @param slotId the ID of the scheduled slot
	 * @param absenceId the ID of the teacher absence to exclude from the check
	 * @return true if a duplicate slot-specific absence exists, false otherwise
	 */
	boolean existsByTeacherIdAndDateAndScheduledSlotIdAndIdNot(Long teacherId, LocalDate date, Long slotId,
			Long absenceId);

	/**
	 * Checks if a full-day absence already exists, excluding a specific absence ID. Used
	 * for Admin updates to prevent collisions while allowing the current record to be
	 * updated.
	 * @param teacherId the ID of the teacher
	 * @param date the date of the teacher absence
	 * @param absenceId the ID of the teacher absence to exclude from the check
	 * @return true if a duplicate full-day absence exists, false otherwise
	 */
	boolean existsByTeacherIdAndDateAndScheduledSlotIsNullAndIdNot(Long teacherId, LocalDate date, Long absenceId);

}

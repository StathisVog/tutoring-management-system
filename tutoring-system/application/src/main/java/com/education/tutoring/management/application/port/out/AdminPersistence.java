package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.admin.*;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for administrative persistence operations related to enrollments.
 */
public interface AdminPersistence {

	/**
	 * Retrieves a list of all enrollments that are currently pending enrollment approval.
	 * @return a list of {@link EnrollmentResponseDTO}
	 */
	List<EnrollmentResponseDTO> getPendingEnrollRequests();

	/**
	 * Approves a pending enrollment request, changing its status to ACTIVE.
	 * @param enrollmentId the ID of the enrollment to approve
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found
	 */
	void approveEnroll(Long enrollmentId) throws ResourceNotFoundException;

	/**
	 * Rejects a pending enrollment request by hard-deleting the record from the database.
	 * @param enrollmentId the ID of the enrollment request to reject
	 * @throws ResourceNotFoundException if the enrollment is not found
	 */
	void rejectEnroll(Long enrollmentId) throws ResourceNotFoundException;

	/**
	 * Retrieves all enrollments that are currently in a pending drop state.
	 * @return a list of {@link EnrollmentResponseDTO}
	 */
	List<EnrollmentResponseDTO> getPendingDropRequests();

	/**
	 * Approves an enrollment drop request by permanently removing the enrollment record.
	 * @param enrollmentId the ID of the enrollment to approve for dropping
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found in
	 * the system
	 */
	void approveDrop(Long enrollmentId) throws ResourceNotFoundException;

	/**
	 * Retrieves a list of students enrolled in a specific scheduled slot, excluding
	 * dropped students.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @return a list of {@link EnrolledStudentDTO} representing the class roster
	 * @throws ResourceNotFoundException if the scheduled slot does not exist
	 */
	List<EnrolledStudentDTO> getEnrolledStudentsBySlot(Long scheduledSlotId) throws ResourceNotFoundException;

	/**
	 * Retrieves a comprehensively filtered list of lesson activities for administrative
	 * auditing. All filtering parameters are optional and stack dynamically.
	 * @param teacherId optional filter by teacher
	 * @param courseId optional filter by course
	 * @param slotId optional filter by specific scheduled slot
	 * @param startDate optional lower bound for the activity date
	 * @param endDate optional upper bound for the activity date
	 * @return a chronologically ordered list of {@link AdminLessonActivityDTO}
	 */
	List<AdminLessonActivityDTO> getLessonActivities(Long teacherId, Long courseId, Long slotId, LocalDate startDate,
			LocalDate endDate);

	/**
	 * Forcefully updates a lesson activity, bypassing standard timeframe restrictions.
	 * Enforces structural integrity constraints (matching day of week, no duplicates).
	 * @param activityId the ID of the activity to update
	 * @param adminUpdateLessonActivityDTO the new data (date and description)
	 * @throws ResourceNotFoundException if the lesson activity is not found
	 * @throws IllegalOperationException if the new date violates structural constraints
	 */
	void updateLessonActivity(Long activityId, AdminUpdateLessonActivityDTO adminUpdateLessonActivityDTO)
			throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Forcefully deletes a lesson activity from the system. This administrative action
	 * completely removes the record, bypassing any timeframe restrictions.
	 * @param activityId the unique identifier of the lesson activity to delete
	 * @throws ResourceNotFoundException if the lesson activity does not exist in the
	 * database
	 */
	void deleteLessonActivity(Long activityId) throws ResourceNotFoundException;

	/**
	 * Retrieves a comprehensively filtered list of tests, including all nested student
	 * results (grades and comments). All filtering parameters are optional and stack
	 * dynamically.
	 * @param teacherId optional filter by the teacher who authored the test
	 * @param courseId optional filter by the course subject
	 * @param startDate optional lower bound for the test date
	 * @param endDate optional upper bound for the test date
	 * @return a chronologically ordered list of {@link AdminTestDTO} containing nested
	 * test results
	 */
	List<AdminTestDTO> getTests(Long teacherId, Long courseId, LocalDate startDate, LocalDate endDate);

	/**
	 * Forcefully updates a test's metadata (date, description) and optionally overrides
	 * specific student test results (grades, comments). Bypasses standard teacher
	 * timeframes. Enforces structural integrity constraints (e.g., no duplicate tests for
	 * the same course on the same date).
	 * @param testId the unique identifier of the test to update
	 * @param updateDTO the payload containing the new test metadata and nested result
	 * changes
	 * @throws ResourceNotFoundException if the test cannot be found in the database
	 * @throws IllegalOperationException if date constraints are violated or if a
	 * requested test result ID does not belong to this test
	 */
	void updateTest(Long testId, AdminUpdateTestDTO updateDTO)
			throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Forcefully deletes a test and all its associated student results from the system.
	 * This administrative action permanently removes the records via database cascading.
	 * @param testId the unique identifier of the test to delete
	 * @throws ResourceNotFoundException if the test does not exist in the database
	 */
	void deleteTest(Long testId) throws ResourceNotFoundException;

	/**
	 * Forcefully removes a specific student's test result from a test. Due to orphan
	 * removal configuration, severing this relationship will permanently delete the test
	 * result record from the database.
	 * @param testId the unique identifier of the parent test
	 * @param testResultId the unique identifier of the nested test result to delete
	 * @throws ResourceNotFoundException if the test is not found, or if the specified
	 * test result does not belong to it
	 */
	void deleteTestResult(Long testId, Long testResultId) throws ResourceNotFoundException;

	/**
	 * Retrieves a comprehensively filtered list of teacher absences.
	 * @param teacherId optional filter by the teacher declaring the absence
	 * @param slotId optional filter by a specific scheduled slot
	 * @param startDate optional lower bound for the absence date
	 * @param endDate optional upper bound for the absence date
	 * @return a chronologically ordered list of {@link AdminTeacherAbsenceDTO}
	 */
	List<AdminTeacherAbsenceDTO> getTeacherAbsences(Long teacherId, Long slotId, LocalDate startDate,
			LocalDate endDate);

	/**
	 * Forcefully updates a teacher's absence record. Bypasses time restrictions (allows
	 * past dates) but enforces structural integrity, including hybrid logic conversions
	 * between full-day and slot-specific absences.
	 * @param absenceId the unique identifier of the absence to update
	 * @param updateDTO the payload containing the new absence details
	 * @throws ResourceNotFoundException if the absence or target slot cannot be found
	 * @throws IllegalOperationException if date constraints or uniqueness rules are
	 * violated
	 */
	void updateTeacherAbsence(Long absenceId, AdminUpdateTeacherAbsenceDTO updateDTO)
			throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Forcefully deletes a teacher's absence record from the system. This administrative
	 * action permanently removes the record, freeing up the schedule and correcting
	 * attendance miscalculations.
	 * @param absenceId the unique identifier of the absence to delete
	 * @throws ResourceNotFoundException if the absence does not exist in the database
	 */
	void deleteTeacherAbsence(Long absenceId) throws ResourceNotFoundException;

}

package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.application.dto.teacher.*;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for teacher-related operations.
 */
public interface TeacherPersistence {

	/**
	 * Saves a newly registered teacher to the database.
	 * @param registerTeacherDTO the validated teacher registration data
	 * @param passwordHash the cryptographically hashed password
	 */
	void saveTeacherFromRegister(RegisterTeacherDTO registerTeacherDTO, String passwordHash);

	/**
	 * Creates a new test and assigns it to students in a specific scheduled slot. The
	 * test can be assigned to the entire class or a specific subset of active students.
	 * @param teacherUsername the username of the teacher creating the test
	 * @param createTestRequestDTO the details of the test and the target assignment
	 * criteria
	 * @throws ResourceNotFoundException if the teacher, course, or scheduled slot is not
	 * found
	 * @throws UnauthorizedActionException if the teacher does not teach the specified
	 * scheduled slot
	 * @throws IllegalOperationException if the slot does not belong to the course, there
	 * are no active students, or if requested students are not active in the slot
	 */
	void createAndAssignTest(String teacherUsername, CreateTestRequestDTO createTestRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Retrieves a list of tests created by the specified teacher within a given date
	 * range. If the date range parameters are not provided, it defaults to the current
	 * school year (September 1st to August 31st).
	 * @param teacherUsername the username of the requesting teacher
	 * @param fromDate the optional start date to filter tests
	 * @param toDate the optional end date to filter tests
	 * @return a list of {@link TestDTO} representing the teacher's created tests
	 * @throws ResourceNotFoundException if the teacher associated with the username does
	 * not exist
	 */
	List<TestDTO> getTeacherTests(String teacherUsername, LocalDate fromDate, LocalDate toDate)
			throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Retrieves all test results (students' exam papers) for a specific test. Ensures
	 * that the requesting teacher is the actual author of the test.
	 * @param teacherUsername the username of the requesting teacher
	 * @param testId the ID of the test
	 * @return a list of {@link TestResultDTO} representing the students' results
	 * @throws ResourceNotFoundException if the test or teacher is not found
	 * @throws UnauthorizedActionException if the teacher is not the author of the test
	 */
	List<TestResultDTO> getTestResultsForTest(String teacherUsername, Long testId)
			throws ResourceNotFoundException, UnauthorizedActionException;

	/**
	 * Updates the grade of a specific test result. Ensures that the requesting teacher is
	 * the author of the test associated with this result.
	 * @param teacherUsername the username of the teacher performing the grading
	 * @param testResultId the ID of the test result to update
	 * @param gradeTestResultDTO the data transfer object containing the new grade
	 * @throws ResourceNotFoundException if the test result or teacher cannot be found
	 * @throws UnauthorizedActionException if the teacher attempts to grade a test they
	 * did not author
	 */
	void updateTestResultGrade(String teacherUsername, Long testResultId, GradeTestResultDTO gradeTestResultDTO)
			throws ResourceNotFoundException, UnauthorizedActionException;

	/**
	 * Creates a new absence record for a teacher. Enforces hybrid logic: if a slotId is
	 * provided, it validates ownership and day-of-week matching before saving a
	 * slot-specific absence. Otherwise, it saves a full-day absence.
	 * @param teacherUsername the username of the teacher declaring the absence
	 * @param absenceRequestDTO the data transfer object containing the absence details
	 * @throws ResourceNotFoundException if the teacher or the targeted scheduled slot is
	 * not found
	 * @throws UnauthorizedActionException if the targeted slot belongs to a different
	 * teacher
	 * @throws IllegalOperationException if validations fail (past date, day mismatch, or
	 * duplicate entry)
	 */
	void createTeacherAbsence(String teacherUsername, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Retrieves all future and present absences declared by a specific teacher. Includes
	 * both full-day absences and cancellations tied to specific scheduled slots.
	 * @param teacherUsername the username of the requesting teacher
	 * @return a list of {@link TeacherAbsenceDTO} representing the absences
	 * @throws ResourceNotFoundException if the teacher cannot be found
	 */
	List<TeacherAbsenceDTO> getFutureTeacherAbsences(String teacherUsername) throws ResourceNotFoundException;

	/**
	 * Updates an existing future or present absence for a teacher. Validates ownership
	 * and ensures no duplicate absence exists if the date or the slot is changed.
	 * @param teacherUsername the username of the teacher performing the update
	 * @param absenceId the ID of the absence to be updated
	 * @param absenceRequestDTO the data transfer object containing the new date, reason,
	 * and slotId
	 * @throws ResourceNotFoundException if the teacher, absence, or slot cannot be found
	 * @throws UnauthorizedActionException if the teacher attempts to update an absence or
	 * assign a slot they don't own
	 * @throws IllegalOperationException if the new state creates a duplicate conflict,
	 * day mismatch, or modifies a past absence
	 */
	void updateTeacherAbsence(String teacherUsername, Long absenceId, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Deletes a future or present absence declared by the teacher. Seamlessly handles
	 * both full-day and slot-specific absence records. Enforces security (ownership) and
	 * rules (cannot delete historical records).
	 * @param teacherUsername the username of the teacher requesting the deletion
	 * @param absenceId the ID of the absence to be deleted
	 * @throws ResourceNotFoundException if the teacher or the specific absence cannot be
	 * found
	 * @throws UnauthorizedActionException if the teacher attempts to delete an absence
	 * belonging to someone else
	 * @throws IllegalOperationException if the absence is in the past and thus considered
	 * a historical record
	 */
	void deleteTeacherAbsence(String teacherUsername, Long absenceId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Retrieves the dynamically generated schedule for a teacher within a specific date
	 * range. Merges static assigned slots with dynamic teacher absences and actively
	 * enrolled student counts.
	 * @param teacherUsername the username of the requesting teacher
	 * @param startDate the start date of the schedule
	 * @param endDate the end date of the schedule
	 * @return a list of {@link TeacherDailyScheduleDTO} representing the daily schedule
	 * @throws ResourceNotFoundException if the teacher is not found
	 */
	List<TeacherDailyScheduleDTO> getTeacherSchedule(String teacherUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException;

	/**
	 * Persists a new lesson activity (log or future plan) for a specific scheduled slot.
	 * Enforces the hybrid time-window constraint: activities cannot be older than 14 days
	 * (frozen state) and cannot be scheduled more than 30 days in advance.
	 * @param teacherUsername the username of the teacher performing the action
	 * @param slotId the ID of the scheduled slot
	 * @param createLessonActivityDTO the details of the activity to create
	 * @throws ResourceNotFoundException if the teacher or slot does not exist
	 * @throws UnauthorizedActionException if the teacher is not assigned to the specified
	 * slot
	 * @throws IllegalOperationException if time boundaries are violated or an entry
	 * already exists
	 */
	void createLessonActivity(String teacherUsername, Long slotId, CreateLessonActivityDTO createLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Retrieves all lesson activities associated with a specific scheduled slot. Enforces
	 * ownership validation to guarantee data privacy across different classes.
	 * @param teacherUsername the username of the teacher performing the request
	 * @param slotId the ID of the scheduled slot
	 * @return a list of {@link LessonActivityDTO} sorted by date descending
	 * (newest/future first)
	 * @throws ResourceNotFoundException if the teacher or the scheduled slot cannot be
	 * found
	 * @throws UnauthorizedActionException if the teacher attempts to access lesson
	 * activities of a slot (class) they do not teach
	 */
	List<LessonActivityDTO> getLessonActivities(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

	/**
	 * Updates an existing lesson activity.
	 * @param teacherUsername the username of the teacher requesting the update
	 * @param activityId the ID of the lesson activity to update
	 * @param updateLessonActivityDTO the updated data payload
	 * @throws ResourceNotFoundException if the activity does not exist
	 * @throws UnauthorizedActionException if the teacher is not assigned to the slot
	 * associated with this lesson activity
	 * @throws IllegalOperationException if the historical state is locked (frozen)
	 */
	void updateLessonActivity(String teacherUsername, Long activityId, UpdateLessonActivityDTO updateLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Deletes a specific lesson activity. Enforces ownership validation and strictly
	 * forbids the deletion of past historical logs.
	 * @param teacherUsername the username of the teacher requesting the deletion
	 * @param activityId the ID of the lesson activity to delete
	 * @throws ResourceNotFoundException if the lesson activity is not found
	 * @throws UnauthorizedActionException if the teacher does not instruct the associated
	 * class
	 * @throws IllegalOperationException if the lesson activity belongs to a date prior to
	 * today
	 */
	void deleteLessonActivity(String teacherUsername, Long activityId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException;

	/**
	 * Retrieves a list of all actively enrolled students for a specific scheduled slot.
	 * Validates that the requesting teacher is the assigned instructor for the slot.
	 * @param teacherUsername the username of the requesting teacher
	 * @param slotId the ID of the scheduled slot
	 * @return a list of {@link ActiveStudentDTO} representing the active students
	 * @throws ResourceNotFoundException if the teacher or slot cannot be found
	 * @throws UnauthorizedActionException if the teacher does not instruct the specified
	 * slot
	 */
	List<ActiveStudentDTO> getActiveStudentsForSlot(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

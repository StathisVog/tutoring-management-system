package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;
import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import com.education.tutoring.management.domain.util.EnrollmentStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface defining persistence operations for Student entities. Acts as an abstraction
 * layer between the service layer and the data access layer.
 */
public interface StudentPersistence {

	/**
	 * Saves a newly registered student to the database.
	 * @param registerStudentDTO the validated student registration data
	 * @param passwordHash the cryptographically hashed password
	 */
	void saveStudentFromRegister(RegisterStudentDTO registerStudentDTO, String passwordHash);

	/**
	 * Retrieves a student by its ID.
	 * @param id the student ID
	 * @return the corresponding student DTO
	 */
	StudentDTO findById(long id);

	/**
	 * Retrieves all students.
	 * @return a list of student DTOs
	 */
	List<StudentDTO> findAll();

	/**
	 * Saves a student entity.
	 * @param studentDTO the student data to persist
	 * @return the saved student DTO
	 */
	StudentDTO save(StudentDTO studentDTO);

	/**
	 * Enrolls a student into a specific scheduled slot.
	 * @param username the username of the student requesting enrollment
	 * @param slotId the ID of the scheduled slot to enroll in
	 * @return an {@link EnrollmentResponseDTO} containing the details of the newly
	 * created enrollment
	 * @throws ResourceNotFoundException if either the student or the specified scheduled
	 * slot is not found
	 * @throws IllegalOperationException if the requested scheduled slot overlaps in time
	 * with another class the student is already enrolled in
	 */
	EnrollmentResponseDTO enrollStudent(String username, Long slotId)
			throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Retrieves enrollments belonging to the student, optionally filtered by status.
	 * @param username the username of the student
	 * @param statuses an optional list of statuses to filter by. If null or empty, all
	 * enrollments are returned.
	 * @return a list of {@link EnrollmentResponseDTO}
	 */
	List<EnrollmentResponseDTO> getMyEnrollments(String username, List<EnrollmentStatus> statuses);

	/**
	 * Submits a request to drop an active enrollment for a specific student. The
	 * enrollment's status is transitioned to a pending state until an administrator
	 * approves it.
	 * @param username the username of the student requesting the drop
	 * @param enrollmentId the unique identifier of the enrollment to be dropped
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found in
	 * the database
	 */
	void requestEnrollmentDrop(String username, Long enrollmentId)
			throws ResourceNotFoundException, UnauthorizedActionException;

	/**
	 * Retrieves the dynamically generated schedule for a student within a specific date
	 * range. Merges static scheduled slots with dynamic teacher absences.
	 * @param studentUsername the username of the requesting student
	 * @param startDate the start date of the schedule
	 * @param endDate the end date of the schedule
	 * @return a list of {@link StudentDailyScheduleDTO} representing the daily schedule
	 * @throws ResourceNotFoundException if the student is not found
	 */
	List<StudentDailyScheduleDTO> getStudentSchedule(String studentUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException;

	/**
	 * Retrieves the test results and pending tests for a specific student.
	 * @param username the username of the student requesting their tests
	 * @return a list of {@link StudentTestResponseDTO} containing the test details and
	 * grades
	 */
	List<StudentTestResponseDTO> getStudentTestResults(String username);

	/**
	 * Retrieves upcoming lesson activities for a student based on their active
	 * enrollments. Filters out past dates to provide a forward-looking homework and
	 * activity feed.
	 * @param studentUsername the username of the authenticated student
	 * @param slotId the optional slot ID filter (can be null for a global feed)
	 * @return a chronologically sorted list of {@link StudentLessonActivityDTO}
	 * (ascending, nearest first)
	 * @throws ResourceNotFoundException if the student does not exist
	 * @throws UnauthorizedActionException if the student requests a slot they are not
	 * actively enrolled in
	 */
	List<StudentLessonActivityDTO> getStudentLessonActivities(String studentUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException;

}

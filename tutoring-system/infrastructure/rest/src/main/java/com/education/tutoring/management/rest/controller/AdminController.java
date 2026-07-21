package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.admin.*;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.admin.*;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.util.Role;
import com.education.tutoring.management.rest.adapter.AdminResourceMapper;
import com.education.tutoring.management.rest.adapter.EnrollmentResourceMapper;
import com.education.tutoring.management.rest.adapter.UserResourceMapper;
import com.education.tutoring.management.rest.model.*;
import com.education.tutoring.management.rest.model.admin.*;
import com.education.tutoring.management.rest.model.user.UserResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller acting as the primary driving adapter for administrator-specific
 * operations. Exposes secured endpoints requiring 'ROLE_ADMIN' authority to orchestrate
 * system-wide use cases, including user management, enrollment lifecycle oversight, and
 * the auditing of educational activities.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "Endpoints for administrative tasks and user management")
public class AdminController {

	private final ActivateUser activateUser;

	private final GetAllUsers getAllUsers;

	private final GetUserById getUserById;

	private final GetUserByUsername getUserByUsername;

	private final DeleteUser deleteUser;

	private final GetPendingEnrollRequests getPendingEnrollRequests;

	private final ApproveEnrollmentRequest approveEnrollmentRequest;

	private final RejectEnrollment rejectEnrollment;

	private final GetPendingDropRequests getPendingDropRequests;

	private final ApproveEnrollmentDrop approveEnrollmentDrop;

	private final GetEnrolledStudentsBySlot getEnrolledStudentsBySlot;

	private final GetAdminLessonActivities getAdminLessonActivities;

	private final UpdateAdminLessonActivity updateAdminLessonActivity;

	private final DeleteAdminLessonActivity deleteAdminLessonActivity;

	private final GetAdminTests getAdminTests;

	private final UpdateAdminTest updateAdminTest;

	private final DeleteAdminTest deleteAdminTest;

	private final DeleteAdminTestResult deleteAdminTestResult;

	private final GetAdminTeacherAbsences getAdminTeacherAbsences;

	private final UpdateAdminTeacherAbsence updateAdminTeacherAbsence;

	private final DeleteAdminTeacherAbsence deleteAdminTeacherAbsence;

	/**
	 * Activates a registered user account, allowing them to log in to the system.
	 * @param id the unique identifier of the user to activate
	 * @return a {@link ResponseEntity} with a success message
	 */
	@Operation(summary = "Activate a user account",
			description = "Enables a registered user account, allowing them to authenticate. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "User successfully activated")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@PutMapping("/users/{id}/activate")
	public ResponseEntity<String> activateUser(@PathVariable Long id) throws ResourceNotFoundException {

		activateUser.execute(id);

		return ResponseEntity.ok("User with ID " + id + " has been successfully activated.");
	}

	/**
	 * Retrieves a list of all users in the system.
	 * @return a list of user details for administrative purposes
	 */
	@Operation(summary = "Get all users",
			description = "Retrieves a comprehensive list of all users. Can be filtered by role. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "List of users successfully retrieved")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping("/users")
	public ResponseEntity<List<UserResource>> getAllUsers(
			@Parameter(description = "Optional filter by user role") @RequestParam(required = false) Role role) {

		List<UserDTO> users = getAllUsers.execute(role);

		List<UserResource> resources = UserResourceMapper.MAPPER.toResources(users);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Retrieves a user by their unique identifier, executing the corresponding use case
	 * and polymorphically mapping the result to the appropriate resource type (e.g.,
	 * StudentResource or TeacherResource).
	 * @param id the unique identifier of the user to retrieve
	 * @return a {@link ResponseEntity} containing the polymorphically mapped
	 * {@link UserResource}
	 * @throws ResourceNotFoundException if no user is found with the provided ID
	 */
	@Operation(summary = "Get user by ID",
			description = "Retrieves comprehensive details for a specific user account. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "User details successfully retrieved")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@GetMapping("/users/{id}")
	public ResponseEntity<UserResource> getUserById(@PathVariable Long id) throws ResourceNotFoundException {

		UserDTO userDTO = getUserById.execute(id);

		UserResource resource = UserResourceMapper.MAPPER.toResource(userDTO);

		return ResponseEntity.ok(resource);
	}

	/**
	 * Retrieves detailed information for a specific user using their username.
	 * @param username the username of the user to search for
	 * @return the user details for administrative purposes
	 * @throws ResourceNotFoundException if the user does not exist
	 */
	@Operation(summary = "Get user by username",
			description = "Retrieves comprehensive details for a specific user account using their username. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "User details successfully retrieved")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@GetMapping("/users/username/{username}")
	public ResponseEntity<UserResource> getUserByUsername(@PathVariable String username)
			throws ResourceNotFoundException {

		UserDTO userDTO = getUserByUsername.execute(username);

		UserResource resource = UserResourceMapper.MAPPER.toResource(userDTO);

		return ResponseEntity.ok(resource);
	}

	/**
	 * Deletes a user from the system.
	 * @param id the unique identifier of the user to delete
	 * @return a {@link ResponseEntity} with no content upon successful deletion
	 * @throws ResourceNotFoundException if the user does not exist
	 */
	@Operation(summary = "Delete user",
			description = "Permanently removes a user from the system. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "User successfully deleted")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal)
			throws ResourceNotFoundException, IllegalOperationException {

		deleteUser.execute(id, principal.getName());

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves all enrollments that are waiting for admin approval to become ACTIVE.
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link EnrollmentResponseResource}
	 */
	@Operation(summary = "Get pending enrollment requests",
			description = "Retrieves a list of all student enrollments that are currently in PENDING_ENROLL status.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved pending enrollment requests")
	@GetMapping("/enrollments/enroll-requests")
	public ResponseEntity<List<EnrollmentResponseResource>> getPendingEnrollRequests() {

		List<EnrollmentResponseDTO> enrollmentResponseDTOs = getPendingEnrollRequests.execute();
		List<EnrollmentResponseResource> resources = EnrollmentResourceMapper.MAPPER
			.toEnrollmentResponseResourceList(enrollmentResponseDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Approves a pending enrollment request. This action changes the enrollment status
	 * from PENDING_ENROLL to ACTIVE.
	 * @param enrollmentId the ID of the enrollment to approve
	 * @return a {@link ResponseEntity} with no content (204)
	 * @throws ResourceNotFoundException if the enrollment does not exist
	 */
	@Operation(summary = "Approve an enrollment request",
			description = "Approves a student's request to enroll in a class. The status transitions to ACTIVE.")
	@ApiResponse(responseCode = "204", description = "Enrollment successfully approved and activated")
	@ApiResponse(responseCode = "400", description = "Invalid enrollment state (not PENDING_ENROLL)")
	@ApiResponse(responseCode = "404", description = "Enrollment not found")
	@PatchMapping("/enrollments/{enrollmentId}/approve-enroll")
	public ResponseEntity<Void> approveEnroll(@PathVariable Long enrollmentId) throws ResourceNotFoundException {

		log.info("Admin approving enrollment for ID: {}", enrollmentId);
		approveEnrollmentRequest.execute(enrollmentId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Rejects a pending enrollment request. This action permanently deletes the request
	 * from the system, freeing up the temporarily held seat in the slot.
	 * @param enrollmentId the ID of the enrollment request to reject
	 * @return a {@link ResponseEntity} with no content (204)
	 * @throws ResourceNotFoundException if the enrollment does not exist
	 */
	@Operation(summary = "Reject an enrollment request",
			description = "Rejects a student's PENDING_ENROLL request. The record is permanently deleted from the database.")
	@ApiResponse(responseCode = "204", description = "Enrollment request successfully rejected and deleted")
	@ApiResponse(responseCode = "400", description = "Invalid enrollment state (not PENDING_ENROLL)")
	@ApiResponse(responseCode = "404", description = "Enrollment not found")
	@DeleteMapping("/enrollments/{enrollmentId}/reject-enroll")
	public ResponseEntity<Void> rejectEnroll(@PathVariable Long enrollmentId) throws ResourceNotFoundException {

		log.info("Admin rejecting enrollment request for ID: {}", enrollmentId);

		rejectEnrollment.execute(enrollmentId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves a list of all enrollment drop requests submitted by students that are
	 * pending approval.
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link EnrollmentResponseResource}
	 */
	@Operation(summary = "Get pending drop requests",
			description = "Returns a list of all enrollments where students have requested to be unassigned from a class.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved list of drop requests")
	@GetMapping("/enrollments/drop-requests")
	public ResponseEntity<List<EnrollmentResponseResource>> getDropRequests() {

		List<EnrollmentResponseDTO> enrollmentResponseDTOs = getPendingDropRequests.execute();

		List<EnrollmentResponseResource> resources = EnrollmentResourceMapper.MAPPER
			.toEnrollmentResponseResourceList(enrollmentResponseDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Approves a pending enrollment drop request. This action softly deletes the
	 * enrollment by updating its status to DROPPED, thereby freeing up capacity in the
	 * associated scheduled slot.
	 * @param enrollmentId the ID of the enrollment to approve for dropping
	 * @return a {@link ResponseEntity} with no content (204)
	 * @throws ResourceNotFoundException if the enrollment does not exist
	 */
	@Operation(summary = "Approve an enrollment drop request",
			description = "Approves a student's request to drop a class. This is a soft delete operation; the enrollment "
					+ "record is kept for historical purposes but its status is changed to DROPPED, releasing the seat in the class.")
	@ApiResponse(responseCode = "204",
			description = "Enrollment drop successfully approved and status changed to DROPPED")
	@ApiResponse(responseCode = "400", description = "Invalid enrollment state (not PENDING_DROP)")
	@ApiResponse(responseCode = "404", description = "Enrollment not found")
	@PatchMapping("/enrollments/{enrollmentId}/approve-drop")
	public ResponseEntity<Void> approveDrop(@PathVariable Long enrollmentId) throws ResourceNotFoundException {

		log.info("Admin approving drop for enrollment ID: {}", enrollmentId);

		approveEnrollmentDrop.execute(enrollmentId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves the class roster (enrolled students) for a specific scheduled slot.
	 * Excludes students who have been officially dropped from the class.
	 * @param slotId the ID of the scheduled slot
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link EnrolledStudentResponseResource}
	 * @throws ResourceNotFoundException if the scheduled slot does not exist
	 */
	@Operation(summary = "Get class roster",
			description = "Retrieves a detailed list of all students currently enrolled in (or pending drop from) a specific scheduled slot. "
					+ "Students with a DROPPED status are excluded. The list is ordered alphabetically by the student's full name.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the class roster")
	@ApiResponse(responseCode = "404", description = "Scheduled slot not found")
	@GetMapping("/slots/{slotId}/students")
	public ResponseEntity<List<EnrolledStudentResponseResource>> getEnrolledStudentsBySlot(@PathVariable Long slotId)
			throws ResourceNotFoundException {

		log.info("Received request to fetch class roster for slot ID: {}", slotId);

		List<EnrolledStudentDTO> rosterDTOs = getEnrolledStudentsBySlot.execute(slotId);

		List<EnrolledStudentResponseResource> response = EnrollmentResourceMapper.MAPPER
			.toEnrolledStudentResponseResourceList(rosterDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves a dynamic and comprehensive list of lesson activities across the entire
	 * system. Designed for administrative oversight, allowing multi-layered optional
	 * filtering.
	 * @param teacherId optional filter to find activities logged by a specific teacher
	 * @param courseId optional filter to find activities for a specific course subject
	 * @param slotId optional filter for a specific class slot
	 * @param startDate optional start date to define a search window
	 * @param endDate optional end date to define a search window
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link AdminLessonActivityResponseResource}
	 */
	@Operation(summary = "Get all lesson activities (Audit view)",
			description = "Retrieves lesson activities across all classes. Supports comprehensive dynamic filtering. "
					+ "Restricted to Admin role. Used for auditing and monitoring educational progress.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered activities")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping("/activities")
	public ResponseEntity<List<AdminLessonActivityResponseResource>> getLessonActivities(
			@Parameter(description = "Filter by teacher ID") @RequestParam(required = false) Long teacherId,
			@Parameter(description = "Filter by course ID") @RequestParam(required = false) Long courseId,
			@Parameter(description = "Filter by scheduled slot ID") @RequestParam(required = false) Long slotId,
			@Parameter(description = "Start date range") @RequestParam(required = false) LocalDate startDate,
			@Parameter(description = "End date range") @RequestParam(required = false) LocalDate endDate) {

		List<AdminLessonActivityDTO> activityDTOs = getAdminLessonActivities.execute(teacherId, courseId, slotId,
				startDate, endDate);

		List<AdminLessonActivityResponseResource> response = AdminResourceMapper.MAPPER
			.toAdminLessonActivityResponseResourceList(activityDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Forcefully updates an existing lesson activity.
	 * @param activityId the ID of the activity to update
	 * @param requestResource the payload containing the new date and description
	 * @return a {@link ResponseEntity} with no content upon successful update
	 * @throws ResourceNotFoundException if the activity is not found
	 * @throws IllegalOperationException if the date violates structural integrity rules
	 */
	@Operation(summary = "Update a lesson activity (Master Override)",
			description = "Forcefully updates the date or description of a lesson activity. Bypasses teacher timeframe restrictions. "
					+ "Restricted to Admin role. Ensures structural integrity (day of week matching and no duplicates).")
	@ApiResponse(responseCode = "204", description = "Lesson activity successfully updated")
	@ApiResponse(responseCode = "400",
			description = "Bad Request - Structural constraint violation (wrong day or duplicate date) or validation error")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Lesson activity not found")
	@PutMapping("/activities/{activityId}")
	public ResponseEntity<Void> updateLessonActivity(@PathVariable Long activityId,
			@Valid @RequestBody AdminUpdateLessonActivityRequestResource requestResource)
			throws ResourceNotFoundException, IllegalOperationException {

		AdminUpdateLessonActivityDTO adminUpdateLessonActivityDTO = AdminResourceMapper.MAPPER
			.toAdminUpdateLessonActivityDTO(requestResource);

		updateAdminLessonActivity.execute(activityId, adminUpdateLessonActivityDTO);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes a lesson activity from the system.
	 * @param activityId the ID of the activity to delete
	 * @return a {@link ResponseEntity} with no content upon successful deletion
	 * @throws ResourceNotFoundException if the activity is not found
	 */
	@Operation(summary = "Delete a lesson activity (Master Delete)",
			description = "Permanently removes a lesson activity from the database. Bypasses teacher timeframe restrictions. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "Lesson activity successfully deleted")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Lesson activity not found")
	@DeleteMapping("/activities/{activityId}")
	public ResponseEntity<Void> deleteLessonActivity(@PathVariable Long activityId) throws ResourceNotFoundException {

		deleteAdminLessonActivity.execute(activityId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves a dynamic and comprehensive list of tests and their respective student
	 * results. Designed for administrative oversight, allowing multi-layered optional
	 * filtering.
	 * @param teacherId optional filter to find tests authored by a specific teacher
	 * @param courseId optional filter to find tests for a specific course subject
	 * @param startDate optional start date to define a search window
	 * @param endDate optional end date to define a search window
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link AdminTestResponseResource}
	 */
	@Operation(summary = "Get all tests and student results (Audit view)",
			description = "Retrieves tests across all courses, including nested student results and grades. "
					+ "Supports comprehensive dynamic filtering. Restricted to Admin role. Used for academic monitoring.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered tests and results")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping("/tests")
	public ResponseEntity<List<AdminTestResponseResource>> getTests(
			@Parameter(description = "Filter by teacher ID") @RequestParam(required = false) Long teacherId,
			@Parameter(description = "Filter by course ID") @RequestParam(required = false) Long courseId,
			@Parameter(description = "Start date range") @RequestParam(required = false) LocalDate startDate,
			@Parameter(description = "End date range") @RequestParam(required = false) LocalDate endDate) {

		List<AdminTestDTO> adminTestDTOs = getAdminTests.execute(teacherId, courseId, startDate, endDate);

		List<AdminTestResponseResource> response = AdminResourceMapper.MAPPER
			.toAdminTestResponseResourceList(adminTestDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Forcefully updates a test's metadata and optionally its nested student results.
	 * @param testId the ID of the test to update
	 * @param requestResource the payload containing the new metadata and optional grade
	 * changes
	 * @return a {@link ResponseEntity} with no content upon successful update
	 * @throws ResourceNotFoundException if the test is not found
	 * @throws IllegalOperationException if date constraints are violated or result IDs
	 * mismatch
	 */
	@Operation(summary = "Update a test and results (Master Override)",
			description = "Forcefully updates the date or description of a test, and optionally updates nested student grades and comments. "
					+ "Restricted to Admin role. Ensures structural integrity (prevents duplicate dates for the same course).")
	@ApiResponse(responseCode = "204", description = "Test and nested results successfully updated")
	@ApiResponse(responseCode = "400",
			description = "Bad Request - Structural constraint violation, mismatched result IDs, or validation error")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Test not found")
	@PutMapping("/tests/{testId}")
	public ResponseEntity<Void> updateTest(@PathVariable Long testId,
			@Valid @RequestBody AdminUpdateTestRequestResource requestResource)
			throws ResourceNotFoundException, IllegalOperationException {

		AdminUpdateTestDTO adminUpdateTestDTO = AdminResourceMapper.MAPPER.toAdminUpdateTestDTO(requestResource);

		updateAdminTest.execute(testId, adminUpdateTestDTO);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes a test and all its associated student results from the system.
	 * @param testId the ID of the test to delete
	 * @return a {@link ResponseEntity} with no content upon successful deletion
	 * @throws ResourceNotFoundException if the test is not found
	 */
	@Operation(summary = "Delete a test (Master Delete)",
			description = "Permanently removes a test and cascades the deletion to all its nested student results. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "Test and all associated results successfully deleted")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Test not found")
	@DeleteMapping("/tests/{testId}")
	public ResponseEntity<Void> deleteTest(@PathVariable Long testId) throws ResourceNotFoundException {

		deleteAdminTest.execute(testId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes a specific student's test result from a parent test.
	 * @param testId the ID of the parent test
	 * @param testResultId the ID of the nested test result to delete
	 * @return a {@link ResponseEntity} with no content upon successful deletion
	 * @throws ResourceNotFoundException if the test or the specific result is not found
	 */
	@Operation(summary = "Delete a specific test result",
			description = "Permanently removes a single student's test result (graded paper) from a test. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "Test result successfully deleted")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Test or nested Test Result not found")
	@DeleteMapping("/tests/{testId}/results/{testResultId}")
	public ResponseEntity<Void> deleteTestResult(@PathVariable Long testId, @PathVariable Long testResultId)
			throws ResourceNotFoundException {

		deleteAdminTestResult.execute(testId, testResultId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves a dynamic and comprehensive list of teacher absences. Designed for
	 * administrative oversight, allowing multi-layered optional filtering.
	 * @param teacherId optional filter to find absences for a specific teacher
	 * @param slotId optional filter to find absences affecting a specific slot
	 * @param startDate optional start date to define a search window
	 * @param endDate optional end date to define a search window
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link AdminTeacherAbsenceResponseResource}
	 */
	@Operation(summary = "Get all teacher absences (Audit view)",
			description = "Retrieves teacher absences system-wide. Supports comprehensive dynamic filtering. "
					+ "Results seamlessly handle both full-day absences (slotId is null) and slot-specific absences.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered teacher absences")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping("/absences")
	public ResponseEntity<List<AdminTeacherAbsenceResponseResource>> getTeacherAbsences(
			@Parameter(description = "Filter by teacher ID") @RequestParam(required = false) Long teacherId,
			@Parameter(description = "Filter by scheduled slot ID") @RequestParam(required = false) Long slotId,
			@Parameter(description = "Start date range") @RequestParam(required = false) LocalDate startDate,
			@Parameter(description = "End date range") @RequestParam(required = false) LocalDate endDate) {

		List<AdminTeacherAbsenceDTO> absenceDTOs = getAdminTeacherAbsences.execute(teacherId, slotId, startDate,
				endDate);

		List<AdminTeacherAbsenceResponseResource> response = AdminResourceMapper.MAPPER
			.toAdminTeacherAbsenceResponseResourceList(absenceDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Forcefully updates a teacher absence, allowing modification of dates, reasons, and
	 * scope (full-day vs slot-specific).
	 * @param absenceId the ID of the teacher absence to update
	 * @param requestResource the payload containing the new absence details
	 * @return a {@link ResponseEntity} with no content upon successful update
	 * @throws ResourceNotFoundException if the absence or slot is not found
	 * @throws IllegalOperationException if structural constraints are violated
	 */
	@Operation(summary = "Update a teacher absence (Master Override)",
			description = "Forcefully updates a teacher absence. Bypasses past-date restrictions but enforces structural integrity. "
					+ "Can seamlessly convert between full-day and slot-specific absences.")
	@ApiResponse(responseCode = "204", description = "Teacher absence successfully updated")
	@ApiResponse(responseCode = "400",
			description = "Bad Request - Structural constraint violation or validation error")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Teacher absence or Scheduled Slot not found")
	@PutMapping("/absences/{absenceId}")
	public ResponseEntity<Void> updateTeacherAbsence(@PathVariable Long absenceId,
			@Valid @RequestBody AdminUpdateTeacherAbsenceRequestResource requestResource)
			throws ResourceNotFoundException, IllegalOperationException {

		AdminUpdateTeacherAbsenceDTO updateDTO = AdminResourceMapper.MAPPER
			.toAdminUpdateTeacherAbsenceDTO(requestResource);

		updateAdminTeacherAbsence.execute(absenceId, updateDTO);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes a teacher absence record from the system.
	 * @param absenceId the ID of the teacher absence to delete
	 * @return a {@link ResponseEntity} with no content upon successful deletion
	 * @throws ResourceNotFoundException if the teacher absence is not found
	 */
	@Operation(summary = "Delete a teacher absence",
			description = "Permanently removes a teacher absence record. Restricted to Admin role. Used to correct mistaken entries or last-minute schedule changes.")
	@ApiResponse(responseCode = "204", description = "Teacher absence successfully deleted")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@ApiResponse(responseCode = "404", description = "Teacher absence not found")
	@DeleteMapping("/absences/{absenceId}")
	public ResponseEntity<Void> deleteTeacherAbsence(@PathVariable Long absenceId) throws ResourceNotFoundException {

		deleteAdminTeacherAbsence.execute(absenceId);

		return ResponseEntity.noContent().build();
	}

}

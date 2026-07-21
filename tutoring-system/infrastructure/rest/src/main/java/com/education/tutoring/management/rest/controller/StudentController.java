package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;
import com.education.tutoring.management.application.port.in.student.*;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.rest.adapter.EnrollmentResourceMapper;
import com.education.tutoring.management.rest.adapter.StudentResourceMapper;
import com.education.tutoring.management.rest.model.*;
import com.education.tutoring.management.rest.model.student.EnrollmentRequestResource;
import com.education.tutoring.management.rest.model.student.StudentDailyScheduleResponseResource;
import com.education.tutoring.management.rest.model.student.StudentLessonActivityResponseResource;
import com.education.tutoring.management.rest.model.student.StudentTestResponseResource;
import io.swagger.v3.oas.annotations.Operation;
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
 * REST controller acting as the primary driving adapter for student-centric operations.
 * Exposes endpoints tailored to the student persona, orchestrating use cases for class
 * enrollment lifecycles, schedule retrieval, and the continuous monitoring of academic
 * progress, including test results and daily lesson activities.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@Tag(name = "Student API", description = "Endpoints for student operations")
public class StudentController {

	private final EnrollStudent enrollStudent;

	private final GetStudentEnrollments getStudentEnrollments;

	private final RequestEnrollmentDrop requestEnrollmentDrop;

	private final GetStudentSchedule getStudentSchedule;

	private final GetStudentTestResults getStudentTestResults;

	private final GetStudentLessonActivities getStudentLessonActivities;

	/**
	 * Enrolls the currently authenticated student into a specific scheduled slot.
	 * @param enrollmentRequestResource the request payload containing the ID of the
	 * scheduled slot
	 * @param principal the authenticated user principal representing the current student
	 * @return a {@link ResponseEntity} containing the {@link EnrollmentResponseResource}
	 * with the enrollment details
	 * @throws ResourceNotFoundException if the specified scheduled slot does not exist
	 * @throws IllegalOperationException if the requested scheduled slot overlaps in time
	 * with another class the student is already enrolled in
	 */
	@Operation(summary = "Enroll in a scheduled class",
			description = "Allows an authenticated student to enroll in a specific scheduled slot. "
					+ "Validates capacity, duplicate enrollments, and time-schedule conflicts.")
	@ApiResponse(responseCode = "200", description = "Successfully enrolled")
	@ApiResponse(responseCode = "400", description = "Already enrolled")
	@ApiResponse(responseCode = "403", description = "Schedule time-conflict detected")
	@ApiResponse(responseCode = "404", description = "Slot or student not found")
	@ApiResponse(responseCode = "409", description = "Capacity exceeded")
	@PostMapping("/me/enrollments")
	public ResponseEntity<EnrollmentResponseResource> enroll(
			@Valid @RequestBody EnrollmentRequestResource enrollmentRequestResource, Principal principal)
			throws ResourceNotFoundException, IllegalOperationException {

		EnrollmentResponseDTO enrollmentResponseDTO = enrollStudent.execute(principal.getName(),
				enrollmentRequestResource.getSlotId());

		EnrollmentResponseResource enrollmentResponseResource = EnrollmentResourceMapper.MAPPER
			.toEnrollmentResponseResource(enrollmentResponseDTO);

		return ResponseEntity.ok(enrollmentResponseResource);
	}

	/**
	 * Retrieves enrollments for the currently authenticated student, with an optional
	 * status filter.
	 * @param principal the authenticated user principal
	 * @param status an optional list of statuses to filter the enrollments
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link EnrollmentResponseResource}
	 */
	@Operation(summary = "Get my enrollments",
			description = "Retrieves the student's enrollments. Can be filtered using query parameters. "
					+ "If no filter is provided, all enrollments (including history) are returned.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved enrollment list")
	@GetMapping("/me/enrollments")
	public ResponseEntity<List<EnrollmentResponseResource>> getMyEnrollments(Principal principal,
			@RequestParam(required = false) List<EnrollmentStatus> status) {

		List<EnrollmentResponseDTO> enrollmentResponseDTOs = getStudentEnrollments.execute(principal.getName(), status);

		List<EnrollmentResponseResource> resources = EnrollmentResourceMapper.MAPPER
			.toEnrollmentResponseResourceList(enrollmentResponseDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Submits a request for the currently authenticated student to drop an enrollment.
	 * The enrollment status will change to PENDING_DROP until approved by an admin.
	 * @param enrollmentId the ID of the enrollment to drop
	 * @param principal the authenticated user principal
	 * @return a {@link ResponseEntity} with no content (204)
	 * @throws ResourceNotFoundException if the enrollment does not exist
	 */
	@Operation(summary = "Request to drop an enrollment",
			description = "Allows a student to initiate a drop request for a specific class. "
					+ "The request moves to a pending state and must be approved by an administrator to take effect.")
	@ApiResponse(responseCode = "204", description = "Drop request successfully submitted")
	@ApiResponse(responseCode = "400", description = "Invalid enrollment state (not active)")
	@ApiResponse(responseCode = "403", description = "Unauthorized: Enrollment does not belong to this student")
	@ApiResponse(responseCode = "404", description = "Enrollment not found")
	@PatchMapping("/me/enrollments/{enrollmentId}/drop")
	public ResponseEntity<Void> requestDrop(@PathVariable Long enrollmentId, Principal principal)
			throws ResourceNotFoundException, UnauthorizedActionException {

		log.info("Student '{}' requesting drop for enrollment ID: {}", principal.getName(), enrollmentId);

		requestEnrollmentDrop.execute(principal.getName(), enrollmentId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint for a logged-in student to retrieve their personal schedule. If no dates
	 * are provided, it returns the schedule for the current week (Monday to Sunday).
	 * Incorporates dynamic events such as teacher absences.
	 * @param principal the security principal containing the logged-in student's username
	 * @param startDate the optional start date of the schedule range
	 * @param endDate the optional end date of the schedule range
	 * @return a {@link ResponseEntity} containing a daily chronological breakdown of the
	 * student's schedule
	 * @throws ResourceNotFoundException if the student cannot be found
	 * @throws IllegalOperationException if the requested date range exceeds 31 days or is
	 * outside the academic year
	 */
	@Operation(summary = "Get student schedule",
			description = "Retrieves the student's dynamic schedule. Defaults to the current week (Monday to Sunday) if dates are omitted. "
					+ "Automatically flags courses as CANCELLED if the teacher is absent. Maximum allowed range is 31 days.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the schedule")
	@ApiResponse(responseCode = "400",
			description = "Requested date range exceeds 31 days or is outside the current academic year")
	@ApiResponse(responseCode = "404", description = "Student not found")
	@GetMapping("/me/schedule")
	public ResponseEntity<List<StudentDailyScheduleResponseResource>> getSchedule(Principal principal,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException {

		String studentUsername = principal.getName();
		log.info("Student '{}' requested schedule from {} to {}", studentUsername, startDate, endDate);

		List<StudentDailyScheduleDTO> studentDailyScheduleDTOS = getStudentSchedule.execute(studentUsername, startDate,
				endDate);

		List<StudentDailyScheduleResponseResource> response = StudentResourceMapper.MAPPER
			.toStudentDailyScheduleResponseResourceList(studentDailyScheduleDTOS);

		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves all test results (graded and pending) for the authenticated student.
	 * @param principal the authenticated user principal representing the current student
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link StudentTestResponseResource}
	 */
	@Operation(summary = "Get my test results",
			description = "Retrieves all tests assigned to the authenticated student, including pending and graded tests.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved test results")
	@GetMapping("/me/test-results")
	public ResponseEntity<List<StudentTestResponseResource>> getMyTestResults(Principal principal) {

		List<StudentTestResponseDTO> testResultsDTOs = getStudentTestResults.execute(principal.getName());

		List<StudentTestResponseResource> responseResources = StudentResourceMapper.MAPPER
			.toStudentTestResponseResourceList(testResultsDTOs);

		return ResponseEntity.ok(responseResources);
	}

	/**
	 * Endpoint for an authenticated student to view their upcoming homework and activity
	 * feed. Supports optional filtering by slotId. If omitted, returns upcoming lesson
	 * activities across all active enrollments. Past activities are excluded to serve as
	 * a focused To-Do list.
	 * @param slotId the optional query parameter to filter by a specific class slot
	 * @param principal the security principal of the logged-in student
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link StudentLessonActivityResponseResource} ordered by date ascending (nearest
	 * first)
	 * @throws ResourceNotFoundException if the student profile is missing
	 * @throws UnauthorizedActionException if the student queries a slot they are not
	 * actively enrolled in
	 */
	@Operation(summary = "Get upcoming lesson activities feed",
			description = "Retrieves a feed of upcoming lesson activities and assignments (acting as a To-Do list). Past activities are excluded. "
					+ "If 'slotId' is specified, returns activities only for that class (requires ACTIVE enrollment). "
					+ "If omitted, returns a global upcoming feed across all active courses.")
	@ApiResponse(responseCode = "200", description = "Successfully fetched the upcoming activities feed")
	@ApiResponse(responseCode = "403", description = "Forbidden - Student is not actively enrolled in this class slot")
	@ApiResponse(responseCode = "404", description = "Student profile resource not found")
	@GetMapping("/me/activities")
	public ResponseEntity<List<StudentLessonActivityResponseResource>> getMyLessonActivities(
			@RequestParam(value = "slotId", required = false) Long slotId, Principal principal)
			throws ResourceNotFoundException, UnauthorizedActionException {

		String studentUsername = principal.getName();
		log.info("Received request from student '{}' to fetch lesson activities timeline.", studentUsername);

		List<StudentLessonActivityDTO> studentLessonActivityDTOs = getStudentLessonActivities.execute(studentUsername,
				slotId);

		List<StudentLessonActivityResponseResource> response = StudentResourceMapper.MAPPER
			.toStudentLessonActivityResponseResourceList(studentLessonActivityDTOs);

		return ResponseEntity.ok(response);
	}

}

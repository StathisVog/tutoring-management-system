package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.teacher.*;
import com.education.tutoring.management.application.port.in.teacher.*;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import com.education.tutoring.management.rest.adapter.TeacherResourceMapper;
import com.education.tutoring.management.rest.adapter.TestResourceMapper;
import com.education.tutoring.management.rest.model.teacher.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller acting as the primary driving adapter for teacher-centric operations.
 * Exposes endpoints tailored to the teaching staff, orchestrating use cases for academic
 * assessments (test creation and grading), timetable management, daily lesson activity
 * tracking, absence reporting, and real-time class roster retrieval.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teachers")
@Tag(name = "Teacher API", description = "Endpoints for teacher operations")
public class TeacherController {

	private final CreateAndAssignTest createAndAssignTest;

	private final GetTeacherTests getTeacherTests;

	private final GetTestResultsByTest getTestResultsByTest;

	private final GradeTestResult gradeTestResult;

	private final CreateTeacherAbsence createTeacherAbsence;

	private final GetFutureTeacherAbsences getFutureTeacherAbsences;

	private final UpdateTeacherAbsence updateTeacherAbsence;

	private final DeleteTeacherAbsence deleteTeacherAbsence;

	private final GetTeacherSchedule getTeacherSchedule;

	private final CreateLessonActivity createLessonActivity;

	private final GetLessonActivities getLessonActivities;

	private final UpdateLessonActivity updateLessonActivity;

	private final DeleteLessonActivity deleteLessonActivity;

	private final GetActiveStudentsForSlot getActiveStudentsForSlot;

	/**
	 * Endpoint for a teacher to create a new test and assign it to students in a specific
	 * class. The teacher can choose to assign it to the entire class or a specific subset
	 * of active students.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param createTestResource the payload containing test details and target assignment
	 * criteria
	 * @return a {@link ResponseEntity} with status 201 (Created) upon successful creation
	 * and assignment
	 * @throws ResourceNotFoundException if the teacher or the scheduled slot cannot be
	 * found in the database
	 * @throws UnauthorizedActionException if the teacher does not have permission to
	 * assign a test to the specified class
	 * @throws IllegalOperationException if the slot does not belong to the course, there
	 * are no active students, or if requested students are not active in the slot
	 */
	@Operation(summary = "Create and assign a new test",
			description = "Allows a logged-in teacher to create a test and assign it to an entire class or specific active students. "
					+ "The teacher must be the assigned instructor for the target class.")
	@ApiResponse(responseCode = "201", description = "Test successfully created and assigned to students")
	@ApiResponse(responseCode = "400",
			description = "Invalid request payload or illegal business operation (e.g., inactive students)")
	@ApiResponse(responseCode = "403",
			description = "Unauthorized action (e.g., trying to assign a test to another teacher's class)")
	@ApiResponse(responseCode = "404", description = "Teacher or scheduled slot not found")
	@PostMapping("/me/tests")
	public ResponseEntity<Void> createTest(Principal principal,
			@Valid @RequestBody CreateTestResource createTestResource)
			throws ResourceNotFoundException, IllegalOperationException, UnauthorizedActionException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting to create a test for Scheduled Slot ID: {}", teacherUsername,
				createTestResource.getScheduledSlotId());

		CreateTestRequestDTO createTestRequestDTO = TestResourceMapper.MAPPER
			.toCreateTestRequestDTO(createTestResource);

		createAndAssignTest.execute(teacherUsername, createTestRequestDTO);

		log.info("Test successfully created and assigned by teacher '{}'", teacherUsername);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Endpoint to retrieve all tests authored by the logged-in teacher within a specific
	 * date range. If no date range is provided, the system defaults to filtering by the
	 * current school year.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param fromDate optional start date query parameter (Format: YYYY-MM-DD)
	 * @param toDate optional end date query parameter (Format: YYYY-MM-DD)
	 * @return a {@link ResponseEntity} containing a list of {@link TestResponseResource}
	 * objects and HTTP status 200 (OK)
	 * @throws ResourceNotFoundException if the authenticated teacher cannot be found
	 * @throws IllegalOperationException if the provided 'fromDate' is chronologically
	 * after 'toDate'
	 */
	@Operation(summary = "Get teacher's tests",
			description = "Retrieves a list of all tests created by the logged-in teacher within a given date range. "
					+ "If the query parameters 'fromDate' and 'toDate' are omitted, the results default to the current school year.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of tests")
	@ApiResponse(responseCode = "400", description = "Invalid date range parameters supplied")
	@ApiResponse(responseCode = "404", description = "Teacher not found")
	@GetMapping("/me/tests")
	public ResponseEntity<List<TestResponseResource>> getTests(Principal principal,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate)
			throws ResourceNotFoundException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting their tests. Date range filter: [from: {}, to: {}]", teacherUsername,
				fromDate, toDate);

		List<TestDTO> testDTOs = getTeacherTests.execute(teacherUsername, fromDate, toDate);

		List<TestResponseResource> responseResources = testDTOs.stream()
			.map(TestResourceMapper.MAPPER::toTestResponseResource)
			.toList();

		log.info("Successfully fetched {} tests for teacher '{}'", responseResources.size(), teacherUsername);

		return ResponseEntity.ok(responseResources);
	}

	/**
	 * Endpoint to retrieve all student results (exam papers) for a specific test. The
	 * teacher must be the author of the specified test.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param testId the path variable representing the test ID
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link TeachersTestResultResource} objects
	 * @throws ResourceNotFoundException if the test cannot be found
	 * @throws UnauthorizedActionException if the requesting teacher is not the author of
	 * the test
	 */
	@Operation(summary = "Get student results for a specific test",
			description = "Retrieves a list of all students and their grades for a given test. The logged-in teacher must be the creator of the test.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the test results")
	@ApiResponse(responseCode = "403",
			description = "Unauthorized action (e.g., attempting to view another teacher's test)")
	@ApiResponse(responseCode = "404", description = "Test not found")
	@GetMapping("/me/tests/{testId}/results")
	public ResponseEntity<List<TeachersTestResultResource>> getTestResults(Principal principal,
			@PathVariable Long testId) throws ResourceNotFoundException, UnauthorizedActionException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting test results for Test ID: {}", teacherUsername, testId);

		List<TestResultDTO> testResults = getTestResultsByTest.execute(teacherUsername, testId);

		List<TeachersTestResultResource> resources = testResults.stream()
			.map(TestResourceMapper.MAPPER::toTeachersTestResultResource)
			.toList();

		log.info("Successfully fetched {} test results for Test ID: {}", resources.size(), testId);
		return ResponseEntity.ok(resources);
	}

	/**
	 * Endpoint for a teacher to update a student's grade for a specific test result. The
	 * teacher must be the author of the test.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param testResultId the path variable representing the test result ID
	 * @param gradeTestResultResource the payload containing the grade
	 * @return a {@link ResponseEntity} with HTTP status 204 (No Content) upon successful
	 * update
	 * @throws ResourceNotFoundException if the test result cannot be found
	 * @throws UnauthorizedActionException if the teacher attempts to grade another
	 * teacher's test
	 */
	@Operation(summary = "Grade a student's test",
			description = "Allows a teacher to assign or update the grade for a specific student's test. "
					+ "The teacher must be the creator of the test. Grades must be between 0.00 and 20.00.")
	@ApiResponse(responseCode = "204", description = "Grade successfully updated")
	@ApiResponse(responseCode = "400", description = "Invalid grade format (e.g., negative or greater than 20)")
	@ApiResponse(responseCode = "403",
			description = "Unauthorized action (e.g., trying to grade another teacher's test)")
	@ApiResponse(responseCode = "404", description = "Test result not found")
	@PatchMapping("/me/test-results/{testResultId}")
	public ResponseEntity<Void> updateGrade(Principal principal, @PathVariable Long testResultId,
			@Valid @RequestBody GradeTestResultResource gradeTestResultResource)
			throws ResourceNotFoundException, UnauthorizedActionException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting to update grade for Test Result ID: {}", teacherUsername, testResultId);

		GradeTestResultDTO gradeTestResultDTO = TestResourceMapper.MAPPER.toGradeTestResultDTO(gradeTestResultResource);

		gradeTestResult.execute(teacherUsername, testResultId, gradeTestResultDTO);

		log.info("Grade successfully updated for Test Result ID: {} by teacher '{}'", testResultId, teacherUsername);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint for a logged-in teacher to declare an absence for a future or present
	 * date. Supports both full-day absences and slot-specific absences.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param absenceResource the payload containing the date, optional reason, and
	 * optional slotId
	 * @return a {@link ResponseEntity} with status 201 (Created) upon successful
	 * registration
	 * @throws ResourceNotFoundException if the authenticated teacher or the specified
	 * slot cannot be found
	 * @throws IllegalOperationException if the date is in the past, the slot day doesn't
	 * match the date, or a duplicate absence exists
	 * @throws UnauthorizedActionException if the teacher attempts to cancel a slot they
	 * do not teach
	 */
	@Operation(summary = "Declare a teacher absence",
			description = "Allows a logged-in teacher to declare an absence. If slotId is provided, it cancels only that specific lesson. "
					+ "If slotId is null, it declares a full-day absence. Past dates and day-of-week mismatches are rejected.")
	@ApiResponse(responseCode = "201", description = "Absence successfully declared")
	@ApiResponse(responseCode = "400",
			description = "Invalid payload, past date, day-of-week mismatch, or duplicate absence declaration")
	@ApiResponse(responseCode = "403", description = "Unauthorized attempt to cancel another teacher's slot")
	@ApiResponse(responseCode = "404", description = "Teacher or scheduled slot not found")
	@PostMapping("/me/absences")
	public ResponseEntity<Void> declareAbsence(Principal principal, @Valid @RequestBody AbsenceResource absenceResource)
			throws ResourceNotFoundException, IllegalOperationException, UnauthorizedActionException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting to declare an absence for date: {}", teacherUsername,
				absenceResource.getDate());

		AbsenceRequestDTO absenceRequestDTO = TeacherResourceMapper.MAPPER.toAbsenceRequestDTO(absenceResource);

		createTeacherAbsence.execute(teacherUsername, absenceRequestDTO);

		log.info("Absence successfully registered for teacher '{}' on date: {}", teacherUsername,
				absenceResource.getDate());

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Endpoint to retrieve all future and present absences for the logged-in teacher.
	 * Past absences are automatically filtered out. Supports both full-day and
	 * slot-specific absences.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link TeacherAbsenceResponseResource} objects
	 * @throws ResourceNotFoundException if the authenticated teacher cannot be found
	 */
	@Operation(summary = "Get future teacher absences",
			description = "Retrieves a chronological list of all present and future absences declared by the logged-in teacher. "
					+ "The list includes both full-day absences and slot-specific cancellations. Past absences are excluded.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of future absences")
	@ApiResponse(responseCode = "404", description = "Teacher not found")
	@GetMapping("/me/absences")
	public ResponseEntity<List<TeacherAbsenceResponseResource>> getFutureAbsences(Principal principal)
			throws ResourceNotFoundException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting their future absences", teacherUsername);

		List<TeacherAbsenceDTO> teacherAbsenceDTOs = getFutureTeacherAbsences.execute(teacherUsername);

		List<TeacherAbsenceResponseResource> resources = teacherAbsenceDTOs.stream()
			.map(TeacherResourceMapper.MAPPER::toTeacherAbsenceResponseResource)
			.toList();

		log.info("Successfully fetched {} future absences for teacher '{}'", resources.size(), teacherUsername);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Endpoint for a logged-in teacher to update an existing future absence. Supports
	 * modifying the date, reason, and the specific slot (hybrid logic). Modifying
	 * past/historical absences is strictly prohibited.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param absenceId the path variable representing the ID of the absence to update
	 * @param absenceResource the payload containing the updated date, reason, and
	 * optional slotId
	 * @return a {@link ResponseEntity} with status 204 (No Content) upon successful
	 * update
	 * @throws ResourceNotFoundException if the absence, teacher, or new slot cannot be
	 * found
	 * @throws UnauthorizedActionException if the teacher tries to modify another
	 * teacher's absence or assign a slot they don't teach
	 * @throws IllegalOperationException if the new date has a conflict, day-of-week
	 * mismatches, or modifying a past record
	 */
	@Operation(summary = "Update an existing absence",
			description = "Allows a teacher to modify the date, reason, and targeted slot of a future absence. "
					+ "Can switch between full-day and slot-specific absences. Cannot modify historical absences or create duplicate conflicts.")
	@ApiResponse(responseCode = "204", description = "Absence successfully updated")
	@ApiResponse(responseCode = "400",
			description = "Invalid payload, past date supplied, duplicate date conflict, day mismatch, or attempting to modify a past record")
	@ApiResponse(responseCode = "403", description = "Unauthorized modification attempt (absence or slot ownership)")
	@ApiResponse(responseCode = "404", description = "Absence or Scheduled Slot not found")
	@PutMapping("/me/absences/{absenceId}")
	public ResponseEntity<Void> updateAbsence(Principal principal, @PathVariable Long absenceId,
			@Valid @RequestBody AbsenceResource absenceResource)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting to update absence ID: {}", teacherUsername, absenceId);

		AbsenceRequestDTO absenceRequestDTO = TeacherResourceMapper.MAPPER.toAbsenceRequestDTO(absenceResource);

		updateTeacherAbsence.execute(teacherUsername, absenceId, absenceRequestDTO);

		log.info("Successfully updated absence ID: {} for teacher '{}'", absenceId, teacherUsername);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint for a logged-in teacher to delete an existing future or present absence.
	 * Works for both full-day absences and slot-specific cancellations. Historical (past)
	 * absences cannot be deleted.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param absenceId the path variable representing the ID of the absence to delete
	 * @return a {@link ResponseEntity} with status 204 (No Content) upon successful
	 * deletion
	 * @throws ResourceNotFoundException if the absence or teacher cannot be found
	 * @throws UnauthorizedActionException if the teacher tries to delete another
	 * teacher's absence
	 * @throws IllegalOperationException if the absence is in the past
	 */
	@Operation(summary = "Delete an existing absence",
			description = "Allows a teacher to delete a future or present absence (whether it is a full-day or slot-specific absence). "
					+ "Historical absences are locked and cannot be deleted.")
	@ApiResponse(responseCode = "204", description = "Absence successfully deleted")
	@ApiResponse(responseCode = "400", description = "Attempting to delete a historical record")
	@ApiResponse(responseCode = "403", description = "Unauthorized deletion attempt")
	@ApiResponse(responseCode = "404", description = "Absence not found")
	@DeleteMapping("/me/absences/{absenceId}")
	public ResponseEntity<Void> deleteAbsence(Principal principal, @PathVariable Long absenceId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' is requesting to delete absence ID: {}", teacherUsername, absenceId);

		deleteTeacherAbsence.execute(teacherUsername, absenceId);

		log.info("Successfully deleted absence ID: {} for teacher '{}'", absenceId, teacherUsername);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint for a logged-in teacher to retrieve their personal schedule. If no dates
	 * are provided, it returns the schedule for the current week (Monday to Sunday).
	 * Incorporates dynamic events such as their own declared absences and actively
	 * enrolled student counts.
	 * @param principal the security principal containing the logged-in teacher's username
	 * @param startDate the optional start date of the schedule range
	 * @param endDate the optional end date of the schedule range
	 * @return a {@link ResponseEntity} containing a daily chronological breakdown of the
	 * teacher's schedule
	 * @throws ResourceNotFoundException if the teacher cannot be found
	 * @throws IllegalOperationException if the requested date range exceeds 31 days or is
	 * outside the academic year
	 */
	@Operation(summary = "Get teacher schedule",
			description = "Retrieves the teacher's dynamic schedule. Defaults to the current week (Monday to Sunday) if dates are omitted. "
					+ "Automatically flags courses as CANCELLED if the teacher has declared an absence for that slot. "
					+ "Includes the count of actively enrolled students per class. Maximum allowed date range is 31 days.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the schedule")
	@ApiResponse(responseCode = "400",
			description = "Requested date range exceeds 31 days or is outside the current academic year")
	@ApiResponse(responseCode = "404", description = "Teacher not found")
	@GetMapping("/me/schedule")
	public ResponseEntity<List<TeacherDailyScheduleResponseResource>> getSchedule(Principal principal,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' requested schedule from {} to {}", teacherUsername, startDate, endDate);

		List<TeacherDailyScheduleDTO> dailyScheduleDTOs = getTeacherSchedule.execute(teacherUsername, startDate,
				endDate);

		List<TeacherDailyScheduleResponseResource> response = TeacherResourceMapper.MAPPER
			.toTeacherDailyScheduleResponseResourceList(dailyScheduleDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Endpoint for an authenticated teacher to record a lesson log or plan a future
	 * syllabus activity. Applies a hybrid time constraint: up to 14 days in the past, or
	 * up to 30 days in the future.
	 * @param slotId the ID of the scheduled slot
	 * @param principal the security principal of the logged-in teacher
	 * @param createLessonActivityResource the incoming payload containing description and
	 * date
	 * @return a {@link ResponseEntity} with status 201 (Created) upon successful
	 * persistence
	 * @throws ResourceNotFoundException if the teacher or slot cannot be found
	 * @throws UnauthorizedActionException if the teacher does not instruct this slot
	 * @throws IllegalOperationException if time bounds [-14, +30] are violated or
	 * duplicate entry exists
	 */
	@Operation(summary = "Create a lesson activity (Log or Plan)",
			description = "Allows a teacher to log past activities (up to 14 days ago) or plan future syllabus/homework (up to 30 days ahead). "
					+ "Logs older than 14 days are considered frozen and cannot be modified or added.")
	@ApiResponse(responseCode = "201", description = "Lesson activity successfully created")
	@ApiResponse(responseCode = "400",
			description = "Invalid payload, time window boundary violated, or duplicate date")
	@ApiResponse(responseCode = "403", description = "Forbidden action - Teacher does not own this scheduled slot")
	@ApiResponse(responseCode = "404", description = "Teacher or Scheduled Slot resource not found")
	@PostMapping("/me/slots/{slotId}/activities")
	public ResponseEntity<Void> createLessonActivity(@PathVariable Long slotId, Principal principal,
			@Valid @RequestBody CreateLessonActivityResource createLessonActivityResource)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Received request from teacher '{}' to log/plan lesson activity for slot ID: {}", teacherUsername,
				slotId);

		CreateLessonActivityDTO createLessonActivityDTO = TeacherResourceMapper.MAPPER
			.toCreateLessonActivityDTO(createLessonActivityResource);

		createLessonActivity.execute(teacherUsername, slotId, createLessonActivityDTO);

		log.info("Lesson activity successfully recorded for slot ID: {} by teacher '{}'", slotId, teacherUsername);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Retrieves the complete timeline of lesson activities (both historical logs and
	 * future syllabus plans). If a slotId query parameter is provided, it returns lesson
	 * activities strictly for that class. If no slotId is provided, it returns a global
	 * timeline of all lesson activities across all classes assigned to the teacher.
	 * @param slotId the optional identifier of the scheduled slot to filter by
	 * @param principal the security principal containing the authenticated teacher's
	 * username
	 * @return a {@link ResponseEntity} containing a chronologically sorted list of
	 * {@link LessonActivityResponseResource}
	 * @throws ResourceNotFoundException if the specified slot or the logged-in teacher
	 * cannot be found
	 * @throws UnauthorizedActionException if the teacher is not assigned to the requested
	 * slot
	 */
	@Operation(summary = "Get lesson activities",
			description = "Retrieves recorded and planned lesson activities. Supports optional filtering by 'slotId'. "
					+ "If no 'slotId' is provided, it returns all activities for all classes assigned to the logged-in teacher.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved lesson activities")
	@ApiResponse(responseCode = "403",
			description = "Forbidden action - Teacher does not own the requested scheduled slot")
	@ApiResponse(responseCode = "404", description = "Teacher or Scheduled Slot resource not found")
	@GetMapping("/me/activities")
	public ResponseEntity<List<LessonActivityResponseResource>> getLessonActivities(
			@RequestParam(value = "slotId", required = false) Long slotId, Principal principal)
			throws ResourceNotFoundException, UnauthorizedActionException {

		List<LessonActivityDTO> activityDTOs = getLessonActivities.execute(principal.getName(), slotId);

		List<LessonActivityResponseResource> response = TeacherResourceMapper.MAPPER
			.toLessonActivityResponseResourceList(activityDTOs);

		return ResponseEntity.ok(response);
	}

	/**
	 * Endpoint for an authenticated teacher to modify an existing lesson activity.
	 * @param activityId the database ID of the activity to modify
	 * @param principal the security principal of the logged-in teacher
	 * @param updateLessonActivityResource the updated payload
	 * @return a {@link ResponseEntity} with status 204 (No Content) upon successful
	 * update
	 * @throws ResourceNotFoundException if the lesson activity cannot be found
	 * @throws UnauthorizedActionException if the teacher does not instruct the class
	 * related to this lesson activity
	 * @throws IllegalOperationException if historical logs are frozen
	 */
	@Operation(summary = "Update a lesson activity",
			description = "Allows a teacher to modify description of an activity lesson. ")
	@ApiResponse(responseCode = "204", description = "Lesson activity successfully updated")
	@ApiResponse(responseCode = "400", description = "Operational constraint violated")
	@ApiResponse(responseCode = "403", description = "Forbidden action - Teacher does not own this class slot")
	@ApiResponse(responseCode = "404", description = "Lesson activity resource not found")
	@PutMapping("/me/activities/{activityId}")
	public ResponseEntity<Void> updateLessonActivity(@PathVariable Long activityId, Principal principal,
			@Valid @RequestBody UpdateLessonActivityResource updateLessonActivityResource)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Received request from teacher '{}' to update lesson activity ID: {}", teacherUsername, activityId);

		UpdateLessonActivityDTO updateLessonActivityDTO = TeacherResourceMapper.MAPPER
			.toUpdateLessonActivityDTO(updateLessonActivityResource);

		updateLessonActivity.execute(teacherUsername, activityId, updateLessonActivityDTO);

		log.info("Lesson activity ID: {} successfully updated by teacher '{}'", activityId, teacherUsername);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint for an authenticated teacher to delete a planned lesson activity.
	 * Deletions are strictly restricted to future or current-day activities to preserve
	 * historical data integrity.
	 * @param activityId the database ID of the activity to delete
	 * @param principal the security principal of the logged-in teacher
	 * @return a {@link ResponseEntity} with status 204 (No Content) upon successful
	 * deletion
	 * @throws ResourceNotFoundException if the activity cannot be found
	 * @throws UnauthorizedActionException if the teacher does not instruct the associated
	 * class
	 * @throws IllegalOperationException if the activity date is in the past
	 */
	@Operation(summary = "Delete a lesson activity",
			description = "Allows a teacher to remove a planned activity. "
					+ "Activities that have already occurred (past dates) cannot be deleted, "
					+ "ensuring the educational log remains intact. They can only be modified via PUT.")
	@ApiResponse(responseCode = "204", description = "Lesson activity successfully deleted")
	@ApiResponse(responseCode = "400",
			description = "Operational constraint violated (e.g., trying to delete a past log)")
	@ApiResponse(responseCode = "403", description = "Forbidden action - Teacher does not own this class slot")
	@ApiResponse(responseCode = "404", description = "Lesson activity resource not found")
	@DeleteMapping("/me/activities/{activityId}")
	public ResponseEntity<Void> deleteLessonActivity(@PathVariable Long activityId, Principal principal)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		String teacherUsername = principal.getName();
		log.info("Received request from teacher '{}' to delete lesson activity ID: {}", teacherUsername, activityId);

		deleteLessonActivity.execute(teacherUsername, activityId);

		log.info("Lesson activity ID: {} successfully deleted by teacher '{}'", activityId, teacherUsername);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint to retrieve all actively enrolled students for a specific class (scheduled
	 * slot). The logged-in teacher must be the assigned instructor for this slot.
	 * * @param principal the security principal
	 * @param slotId the ID of the scheduled slot
	 * @return a {@link ResponseEntity} containing a list of {@link ActiveStudentResource}
	 * @throws ResourceNotFoundException if the slot cannot be found
	 * @throws UnauthorizedActionException if the teacher doesn't teach this class
	 */
	@Operation(summary = "Get active students for a class",
			description = "Retrieves a list of all actively enrolled students for a specific scheduled slot. "
					+ "Used primarily for assigning specific students to a new test.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of students")
	@ApiResponse(responseCode = "403", description = "Forbidden action - Teacher does not own this class slot")
	@ApiResponse(responseCode = "404", description = "Scheduled slot or Teacher not found")
	@GetMapping("/me/slots/{slotId}/students")
	public ResponseEntity<List<ActiveStudentResource>> getActiveStudentsForSlot(Principal principal,
			@PathVariable Long slotId) throws ResourceNotFoundException, UnauthorizedActionException {

		String teacherUsername = principal.getName();
		log.info("Teacher '{}' requesting active students for Scheduled Slot ID: {}", teacherUsername, slotId);

		List<ActiveStudentDTO> studentDTOs = getActiveStudentsForSlot.execute(teacherUsername, slotId);

		List<ActiveStudentResource> resources = TeacherResourceMapper.MAPPER.toActiveStudentResourceList(studentDTOs);

		log.info("Successfully fetched {} active students for slot ID: {}", resources.size(), slotId);
		return ResponseEntity.ok(resources);
	}

}

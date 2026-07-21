package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.application.dto.teacher.*;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UnauthorizedActionException;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.domain.util.Role;
import com.education.tutoring.management.repository.jpa.entity.*;
import com.education.tutoring.management.repository.jpa.mapper.EntityMapper;
import com.education.tutoring.management.repository.jpa.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * JPA-based implementation of the {@link TeacherPersistence} output port. Acts as the
 * central persistence adapter orchestrating all teacher-centric domain operations.
 * Handles complex, multi-entity transactions spanning teacher registration, profile
 * management, course assignments, timetable scheduling, academic grading (tests and
 * results), daily lesson tracking, and absence management across the educational
 * ecosystem.
 */
@Service
@AllArgsConstructor
public class TeacherPersistenceImpl implements TeacherPersistence {

	private final TeacherRepository teacherRepository;

	private final ScheduledSlotRepository scheduledSlotRepository;

	private final EnrollmentRepository enrollmentRepository;

	private final TestRepository testRepository;

	private final TestResultRepository testResultRepository;

	private final TeacherAbsenceRepository teacherAbsenceRepository;

	private final LessonActivityRepository lessonActivityRepository;

	private final CourseRepository courseRepository;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Persists a new teacher entity to the database during the registration process.
	 * Initializes default system attributes (such as the TEACHER role and disabled status
	 * pending approval), and processes the teacher's selected eligible courses to
	 * establish initial preferences before saving.
	 * @param registerTeacherDTO the validated teacher registration data containing
	 * personal info and course preferences
	 * @param passwordHash the cryptographically hashed password
	 */
	@Override
	@Transactional
	public void saveTeacherFromRegister(RegisterTeacherDTO registerTeacherDTO, String passwordHash) {

		Teacher teacher = mapper.toTeacherEntityFromRegister(registerTeacherDTO);

		teacher.setPasswordHash(passwordHash);
		teacher.setEnabled(false);
		teacher.setRole(Role.TEACHER);

		if (registerTeacherDTO.getEligibleCourseIds() != null && !registerTeacherDTO.getEligibleCourseIds().isEmpty()) {

			List<Course> selectedCourses = courseRepository.findAllById(registerTeacherDTO.getEligibleCourseIds());

			selectedCourses.forEach(teacher::addEligibleCourse);
		}

		teacherRepository.save(teacher);
	}

	/**
	 * Creates a new test and assigns it to students within a specific scheduled slot.
	 * This method supports two assignment scopes: 1. Whole Class: If no specific student
	 * IDs are provided, the test is assigned to all ACTIVE students. 2. Specific
	 * Students: If a list of student IDs is provided, the test is assigned ONLY to those
	 * specific ACTIVE students.
	 * @param teacherUsername the username of the teacher creating the test
	 * @param createTestRequestDTO the details of the test and the target assignment
	 * criteria
	 * @throws ResourceNotFoundException if the specified teacher or scheduled slot cannot
	 * be found in the database
	 * @throws UnauthorizedActionException if the requesting teacher is not the assigned
	 * instructor for the target scheduled slot
	 * @throws IllegalOperationException if the slot does not match the course, if the
	 * test date violates constraints (wrong day of week or duplicate test), or if
	 * requested students are not active in the slot
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createAndAssignTest(String teacherUsername, CreateTestRequestDTO createTestRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(createTestRequestDTO.getScheduledSlotId())
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Scheduled slot '%s' not found.", createTestRequestDTO.getScheduledSlotId())));

		// Step 1: Security Validation - Ensure the requesting teacher is actually
		// assigned to teach this specific class (slot)
		if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to assign a test to a class you do not teach.");
		}

		// Step 2: Data Integrity Validation - Verify that the scheduled slot legitimately
		// belongs to the requested Course
		if (!scheduledSlot.getCourse().getId().equals(createTestRequestDTO.getCourseId())) {
			throw new IllegalOperationException(
					"The specified scheduled slot does not belong to the specified course.");
		}

		// Step 3: Structural Constraint - Day of Week MUST match the scheduled slot's day
		if (createTestRequestDTO.getDate().getDayOfWeek() != scheduledSlot.getDayOfWeek()) {
			throw new IllegalOperationException(
					String.format("Date mismatch. The class is scheduled on a %s, but the provided date is a %s.",
							scheduledSlot.getDayOfWeek(), createTestRequestDTO.getDate().getDayOfWeek()));
		}

		// Step 4: Structural Constraint - No duplicate tests for the same course on the
		// same date
		boolean duplicateExists = testRepository.existsByCourseIdAndDate(scheduledSlot.getCourse().getId(),
				createTestRequestDTO.getDate());

		if (duplicateExists) {
			throw new IllegalOperationException(String.format("A test already exists for course '%s' on %s.",
					scheduledSlot.getCourse().getTitle(), createTestRequestDTO.getDate()));
		}

		// Step 5: Fetch all students currently holding an ACTIVE enrollment in this
		// scheduled slot
		List<Enrollment> activeEnrollments = enrollmentRepository
			.findAllByScheduledSlotIdAndStatus(scheduledSlot.getId(), EnrollmentStatus.ACTIVE);

		if (activeEnrollments.isEmpty()) {
			throw new IllegalOperationException(
					"Cannot create test. There are no active students in this scheduled slot.");
		}

		List<Student> targetStudents;

		if (createTestRequestDTO.getStudentIds() == null || createTestRequestDTO.getStudentIds().isEmpty()) {

			// Case A: No specific IDs provided. Map all active enrollments to the target
			// list (Whole Class Assignment).
			targetStudents = activeEnrollments.stream().map(Enrollment::getStudent).toList();
		}
		else {

			// Case B: Specific IDs provided. Filter the active enrollments to find only
			// the requested students.
			targetStudents = activeEnrollments.stream()
				.map(Enrollment::getStudent)
				.filter(student -> createTestRequestDTO.getStudentIds().contains(student.getId()))
				.toList();

			// Step 6: Strict Validation - Ensure every requested student ID was found
			// among the active enrollments.
			// This prevents assigning tests to dropped students or students from other
			// classes.
			if (targetStudents.size() != createTestRequestDTO.getStudentIds().size()) {
				throw new IllegalOperationException(
						"One or more requested students are not active in this scheduled slot.");
			}
		}

		// Step 7: Build the main Test entity.
		Test test = Test.builder()
			.course(scheduledSlot.getCourse())
			.scheduledSlot(scheduledSlot)
			.author(teacher)
			.date(createTestRequestDTO.getDate())
			.description(createTestRequestDTO.getDescription())
			.build();

		// Step 8: Create an ungraded wrapper (TestResult) for each target student and
		// link it bidirectionally.
		for (Student student : targetStudents) {
			TestResult result = TestResult.builder()
				.student(student)
				// 'grade' is deliberately left null here as the test has not been graded
				// yet
				.build();

			test.addTestResult(result);
		}

		// Step 9: Persist the Test. With 'CascadeType.ALL' on the entity, all associated
		// TestResults are saved automatically.
		testRepository.save(test);
	}

	/**
	 * Retrieves a list of tests authored by the specified teacher within a given date
	 * range. If the date parameters are not provided, the search defaults to the current
	 * school year.
	 * @param teacherUsername the username of the requesting teacher
	 * @param fromDate the optional start date to filter tests
	 * @param toDate the optional end date to filter tests
	 * @return a list of {@link TestDTO} objects representing the tests found within the
	 * criteria
	 * @throws ResourceNotFoundException if the teacher associated with the provided
	 * username cannot be found in the database
	 * @throws IllegalOperationException if the filtering date range is logically invalid
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TestDTO> getTeacherTests(String teacherUsername, LocalDate fromDate, LocalDate toDate)
			throws ResourceNotFoundException, IllegalOperationException {

		if (teacherRepository.findByUsername(teacherUsername).isEmpty()) {
			throw new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername));
		}

		LocalDate startDate = fromDate;
		LocalDate endDate = toDate;

		if (startDate == null || endDate == null) {
			LocalDate today = LocalDate.now();
			int currentYear = today.getYear();
			int currentMonth = today.getMonthValue();

			// School year logic: Starts Sept 1st, Ends Aug 31st
			if (currentMonth >= 9) {

				// If we are in Sep, Oct, Nov, Dec -> School year started THIS year
				startDate = LocalDate.of(currentYear, 9, 1);
				endDate = LocalDate.of(currentYear + 1, 8, 31);
			}
			else {

				// If we are in Jan to Aug -> School year started LAST year
				startDate = LocalDate.of(currentYear - 1, 9, 1);
				endDate = LocalDate.of(currentYear, 8, 31);
			}
		}

		// Step 3: Prevent logical errors (e.g., fromDate being after toDate)
		if (startDate.isAfter(endDate)) {
			throw new IllegalOperationException("The start date cannot be after the end date.");
		}

		List<Test> tests = testRepository.findAllByAuthorUsernameAndDateBetweenOrderByDateDesc(teacherUsername,
				startDate, endDate);

		return tests.stream().map(test -> {
			TestDTO testDTO = mapper.toTestDTO(test);

			// Calculating Total Students
			int total = test.getTestResults().size();

			// Calculation of Graded
			int graded = (int) test.getTestResults().stream().filter(result -> result.getGrade() != null).count();

			testDTO.setTotalStudentsCount(total);
			testDTO.setGradedStudentsCount(graded);

			return testDTO;
		}).toList();
	}

	/**
	 * Retrieves all test results (student exam papers) associated with a specific test.
	 * Enforces security by verifying that the requesting teacher is the legitimate author
	 * of the test.
	 * @param teacherUsername the username of the requesting teacher
	 * @param testId the ID of the test for which results are being requested
	 * @return a list of {@link TestResultDTO} representing the students' results for the
	 * specified test
	 * @throws ResourceNotFoundException if the specified teacher or test cannot be found
	 * in the database
	 * @throws UnauthorizedActionException if the requesting teacher is not the author of
	 * the test
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TestResultDTO> getTestResultsForTest(String teacherUsername, Long testId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		Test test = testRepository.findById(testId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Test '%d' not found.", testId)));

		// Security Validation: Check if the teacher is the author of this test
		if (!test.getAuthor().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to view results for a test you did not create.");
		}

		List<TestResult> results = testResultRepository.findAllByTestIdWithStudent(testId);

		return results.stream().map(mapper::toTestResultDTO).toList();
	}

	/**
	 * Updates the grade for a specific student's test result. Applies strict security
	 * checks to ensure the teacher attempting the grading is the actual creator of the
	 * test.
	 * @param teacherUsername the username of the teacher performing the grading
	 * @param testResultId the ID of the test result to update
	 * @param gradeTestResultDTO the data transfer object containing the new grade to be
	 * assigned
	 * @throws ResourceNotFoundException if the teacher or the specific test result cannot
	 * be found
	 * @throws UnauthorizedActionException if the test associated with this result was not
	 * created by the requesting teacher
	 */
	@Override
	@Transactional
	public void updateTestResultGrade(String teacherUsername, Long testResultId, GradeTestResultDTO gradeTestResultDTO)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		TestResult testResult = testResultRepository.findById(testResultId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Test result '%d' not found.", testResultId)));

		// Security Validation: Check if the test of this testResult belongs to the
		// teacher making the request
		if (!testResult.getTest().getAuthor().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException("You do not have permission to grade a test you did not create.");
		}

		testResult.setGrade(gradeTestResultDTO.getGrade());
		testResult.setComments(gradeTestResultDTO.getComments());

		testResultRepository.save(testResult);
	}

	/**
	 * Declares a new absence for a teacher. Supports both full-day absences and
	 * slot-specific absences. Enforces rules to ensure the teacher owns the specified
	 * slot, the slot actually occurs on the given date, and prevents duplicate absence
	 * declarations.
	 * @param teacherUsername the username of the requesting teacher
	 * @param absenceRequestDTO the payload containing the date, optional reason, and
	 * optional slotId
	 * @throws ResourceNotFoundException if the teacher or the specific scheduled slot is
	 * not found
	 * @throws UnauthorizedActionException if the teacher attempts to cancel a slot they
	 * do not teach
	 * @throws IllegalOperationException if the date is in the past, the slot doesn't
	 * match the day of the week, or an absence already exists
	 */
	@Override
	@Transactional
	public void createTeacherAbsence(String teacherUsername, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		LocalDate date = absenceRequestDTO.getDate();
		Long requestedSlotId = absenceRequestDTO.getSlotId();

		// Business Validation: Date cannot be in the past
		if (date.isBefore(LocalDate.now())) {
			throw new IllegalOperationException("Cannot declare an absence for a past date.");
		}

		ScheduledSlot slotToCancel = null;

		// Handle Slot-Specific Validation (Hybrid Logic)
		if (requestedSlotId != null) {

			slotToCancel = scheduledSlotRepository.findById(requestedSlotId)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Scheduled slot '%d' not found.", requestedSlotId)));

			validateSlotForAbsence(slotToCancel, date, teacher.getId());
		}
		else {

			// Check if the teacher actually has ANY classes on this day of the week
			boolean hasClassesOnThisDay = scheduledSlotRepository.existsByTeacherIdAndDayOfWeek(teacher.getId(),
					date.getDayOfWeek());
			if (!hasClassesOnThisDay) {
				throw new IllegalOperationException(
						"You cannot declare a full-day absence on a day where you have no scheduled classes.");
			}
		}

		// Duplicate Check
		if (requestedSlotId != null) {

			// Check if this specific slot is already canceled on this date
			if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotId(teacher.getId(), date,
					requestedSlotId)) {
				throw new IllegalOperationException(
						"An absence has already been declared for this specific lesson on this date.");
			}
		}
		else {

			// Check if a FULL DAY absence already exists for this date
			if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotIsNull(teacher.getId(), date)) {
				throw new IllegalOperationException("A full-day absence has already been declared for this date.");
			}
		}

		// Build and Persist the Entity
		TeacherAbsence newAbsence = TeacherAbsence.builder()
			.teacher(teacher)
			.date(date)
			.reason(absenceRequestDTO.getReason())
			// Will be null for full-day,
			// or the specific entity for slot-specific
			.scheduledSlot(slotToCancel)
			.build();

		teacherAbsenceRepository.save(newAbsence);
	}

	/**
	 * Retrieves a chronological list of all present and future absences declared by a
	 * specific teacher. Automatically filters out any absences that occurred prior to the
	 * current date. Fetches both hybrid states: full-day absences and slot-specific
	 * absences.
	 * @param teacherUsername the username of the requesting teacher
	 * @return a list of {@link TeacherAbsenceDTO} representing the teacher's upcoming
	 * absences
	 * @throws ResourceNotFoundException if the specified teacher cannot be found in the
	 * database
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TeacherAbsenceDTO> getFutureTeacherAbsences(String teacherUsername) throws ResourceNotFoundException {

		if (teacherRepository.findByUsername(teacherUsername).isEmpty()) {
			throw new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername));
		}

		LocalDate today = LocalDate.now();

		List<TeacherAbsence> teacherAbsences = teacherAbsenceRepository
			.findAllByTeacherUsernameAndDateGreaterThanEqualOrderByDateAsc(teacherUsername, today);

		return teacherAbsences.stream().map(mapper::toTeacherAbsenceDTO).toList();
	}

	/**
	 * Updates the date, reason, and targeted slot of an existing teacher absence in the
	 * database. Applies strict security checks for ownership and business validations to
	 * prevent the modification of past (historical) absences, day-of-week mismatches, and
	 * the creation of duplicate absences.
	 * @param teacherUsername the username of the teacher performing the update
	 * @param absenceId the ID of the absence to be updated
	 * @param absenceRequestDTO the data transfer object containing the new state
	 * @throws ResourceNotFoundException if the teacher, absence, or slot cannot be found
	 * @throws UnauthorizedActionException if the absence or new slot does not belong to
	 * the requesting teacher
	 * @throws IllegalOperationException if the absence is in the past, day mismatch, or
	 * duplicate conflict
	 */
	@Override
	@Transactional
	public void updateTeacherAbsence(String teacherUsername, Long absenceId, AbsenceRequestDTO absenceRequestDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		TeacherAbsence teacherAbsence = teacherAbsenceRepository.findById(absenceId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Absence '%d' not found.", absenceId)));

		// Security Validation: Ownership
		if (!teacherAbsence.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException("You do not have permission to modify this absence.");
		}

		// Business Validation: Prevent modifying historical records
		if (teacherAbsence.getDate().isBefore(LocalDate.now())) {
			throw new IllegalOperationException("Historical absences cannot be modified.");
		}

		// newDate & newSlotId comes from Request

		LocalDate newDate = absenceRequestDTO.getDate();
		Long newSlotId = absenceRequestDTO.getSlotId();

		ScheduledSlot newSlot = null;

		// New Slot Validation (Hybrid Logic)
		if (newSlotId != null) {
			newSlot = scheduledSlotRepository.findById(newSlotId)
				.orElseThrow(() -> new ResourceNotFoundException("Scheduled slot not found."));

			validateSlotForAbsence(newSlot, newDate, teacher.getId());
		}
		else {

			// Check if the teacher actually has ANY classes on this day of the week
			boolean hasClassesOnThisDay = scheduledSlotRepository.existsByTeacherIdAndDayOfWeek(teacher.getId(),
					newDate.getDayOfWeek());
			if (!hasClassesOnThisDay) {
				throw new IllegalOperationException(
						"You cannot declare a full-day absence on a day where you have no scheduled classes.");
			}
		}

		// Duplicate Check (Only run if Date OR Slot changed)
		Long oldSlotId = teacherAbsence.getScheduledSlot() != null ? teacherAbsence.getScheduledSlot().getId() : null;

		boolean dateChanged = !teacherAbsence.getDate().equals(newDate);

		// "Objects.equals" for safe comparison (nulls)
		boolean slotChanged = !Objects.equals(oldSlotId, newSlotId);

		if (dateChanged || slotChanged) {

			if (newSlotId != null) {
				if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotId(teacher.getId(), newDate,
						newSlotId)) {
					throw new IllegalOperationException(
							"An absence has already been declared for this specific lesson on the new date.");
				}
			}
			else {
				if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotIsNull(teacher.getId(), newDate)) {
					throw new IllegalOperationException(
							"A full-day absence has already been declared for the new date.");
				}
			}
		}

		// Apply Updates to the Entity
		teacherAbsence.setDate(newDate);
		teacherAbsence.setReason(absenceRequestDTO.getReason());
		// It can be null or Entity
		teacherAbsence.setScheduledSlot(newSlot);

		teacherAbsenceRepository.save(teacherAbsence);
	}

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
	@Override
	@Transactional
	public void deleteTeacherAbsence(String teacherUsername, Long absenceId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		TeacherAbsence teacherAbsence = teacherAbsenceRepository.findById(absenceId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Absence '%d' not found.", absenceId)));

		// Security Validation: Ownership
		if (!teacherAbsence.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException("You do not have permission to delete this absence.");
		}

		// Business Validation: Prevent deleting historical records
		if (teacherAbsence.getDate().isBefore(LocalDate.now())) {
			throw new IllegalOperationException("Historical absences cannot be deleted.");
		}

		teacherAbsenceRepository.delete(teacherAbsence);
	}

	/**
	 * Retrieves the dynamically generated schedule for a teacher within a specific date
	 * range. This method merges three critical pieces of data: 1. The teacher's assigned
	 * weekly slots. 2. The teacher's declared absences (hybrid logic: full-day or
	 * slot-specific). 3. The exact count of actively enrolled students per slot
	 * @param teacherUsername the username of the teacher requesting their schedule
	 * @param startDate the start date of the requested schedule range
	 * @param endDate the end date of the requested schedule range
	 * @return a chronologically ordered list of {@link TeacherDailyScheduleDTO}
	 * @throws ResourceNotFoundException if the requesting teacher cannot be found in the
	 * database
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TeacherDailyScheduleDTO> getTeacherSchedule(String teacherUsername, LocalDate startDate,
			LocalDate endDate) throws ResourceNotFoundException {

		// Step 1: Verify the existence of the requesting teacher
		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		// Step 2: Fetch the baseline data from the database
		List<ScheduledSlot> activeSlots = scheduledSlotRepository.findAllByTeacherIdWithCourse(teacher.getId());
		List<TeacherAbsence> absences = teacherAbsenceRepository.findAllByTeacherIdAndDateBetween(teacher.getId(),
				startDate, endDate);

		// Step 3: Fetch enrolled student counts
		List<Object[]> rawCounts = enrollmentRepository.countEnrolledStudentsPerSlotForTeacher(teacher.getId(),
				List.of(EnrollmentStatus.ACTIVE));

		// Convert the List of Object arrays [slotId, count] into a highly efficient
		// Map<SlotId, Count>
		Map<Long, Integer> studentCountMap = new HashMap<>();
		for (Object[] row : rawCounts) {
			Long slotId = (Long) row[0];
			Long count = (Long) row[1];
			studentCountMap.put(slotId, count.intValue());
		}

		// Initialize the main list that will hold the final constructed schedule
		List<TeacherDailyScheduleDTO> schedule = new ArrayList<>();

		// Step 4: Iterate sequentially through each date in the provided range
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

			DayOfWeek currentDayOfWeek = date.getDayOfWeek();
			List<TeacherScheduleSlotDTO> dailySlots = new ArrayList<>();

			// Step 5: Iterate through all the teacher's static assigned slots
			for (ScheduledSlot scheduledSlot : activeSlots) {

				if (scheduledSlot.getDayOfWeek() == currentDayOfWeek) {

					LocalDate currentDate = date;

					// Step 6: Hybrid dynamic cancellation check (Teacher Absences).
					// Find if an absence exists for this date AND (it is a full-day
					// absence OR it matches this specific slot)
					TeacherAbsence matchingAbsence = absences.stream()
						.filter(a -> a.getDate().equals(currentDate) && (a.getScheduledSlot() == null
								|| a.getScheduledSlot().getId().equals(scheduledSlot.getId())))
						.findFirst()
						.orElse(null);

					String status = "SCHEDULED";
					String cancelReason = null;

					if (matchingAbsence != null) {
						status = "CANCELLED";
						cancelReason = "Teacher absence: " + matchingAbsence.getReason();
					}

					// Step 7: Retrieve the pre-calculated student count from our Map
					// (Defaults to 0 if no active students)
					int enrolledCount = studentCountMap.getOrDefault(scheduledSlot.getId(), 0);

					// Step 8: Build the Data Transfer Object (DTO)
					TeacherScheduleSlotDTO scheduleSlotDTO = TeacherScheduleSlotDTO.builder()
						.slotId(scheduledSlot.getId())
						.courseTitle(scheduledSlot.getCourse().getTitle())
						.courseId(scheduledSlot.getCourse().getId())
						.startTime(scheduledSlot.getStartTime())
						.endTime(scheduledSlot.getEndTime())
						.classroom(scheduledSlot.getClassroom())
						.status(status)
						.cancelReason(cancelReason)
						.enrolledStudentsCount(enrolledCount)
						.build();

					dailySlots.add(scheduleSlotDTO);
				}
			}

			// Step 9: Sort the daily slots chronologically by their start time
			dailySlots.sort(Comparator.comparing(TeacherScheduleSlotDTO::getStartTime));

			// Step 10: Wrap the daily slots into the DailyScheduleDTO
			TeacherDailyScheduleDTO dailySchedule = TeacherDailyScheduleDTO.builder()
				.date(date)
				.dayOfWeek(currentDayOfWeek)
				.slots(dailySlots)
				.build();

			schedule.add(dailySchedule);
		}

		return schedule;
	}

	/**
	 * Persists a new lesson activity for a specific scheduled slot. Validates ownership
	 * and applies the Hybrid Time Window Rule: - Past Limit: Up to 14 days in the past
	 * (Allows corrections, protects frozen historical data). - Future Limit: Up to 30
	 * days in the future (Allows homework/syllabus planning).
	 * @param teacherUsername the username of the teacher performing the action
	 * @param slotId the ID of the scheduled slot
	 * @param createLessonActivityDTO the details of the activity to create
	 * @throws ResourceNotFoundException if the teacher or scheduled slot is not found
	 * @throws UnauthorizedActionException if the teacher does not teach this specific
	 * slot (class)
	 * @throws IllegalOperationException if time rules are violated or duplicate entry
	 * exists
	 */
	@Override
	@Transactional
	public void createLessonActivity(String teacherUsername, Long slotId,
			CreateLessonActivityDTO createLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(slotId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Scheduled slot with ID %d not found.", slotId)));

		// Security Validation - Ensure the teacher owns this slot
		if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to log or plan activity for a class you do not teach.");
		}

		// Business Validation - Hybrid Time Window Constraints
		LocalDate today = LocalDate.now();
		LocalDate pastLimit = today.minusDays(14);
		LocalDate futureLimit = today.plusDays(30);

		if (createLessonActivityDTO.getDate().isBefore(pastLimit)) {
			throw new IllegalOperationException(
					"Cannot record lesson activity older than 14 days. Historical logs are frozen.");
		}

		if (createLessonActivityDTO.getDate().isAfter(futureLimit)) {
			throw new IllegalOperationException("Cannot schedule lesson activity more than 30 days in advance.");
		}

		// Business Validation - Day of the week matching
		if (!createLessonActivityDTO.getDate().getDayOfWeek().equals(scheduledSlot.getDayOfWeek())) {
			throw new IllegalOperationException(String.format(
					"Invalid date. The selected class takes place on a %s, but the provided date (%s) is a %s.",
					scheduledSlot.getDayOfWeek(), createLessonActivityDTO.getDate(),
					createLessonActivityDTO.getDate().getDayOfWeek()));
		}

		// Business Validation - Prevent duplicate logs for the same slot and date
		boolean activityExists = lessonActivityRepository.existsByScheduledSlotIdAndDate(slotId,
				createLessonActivityDTO.getDate());
		if (activityExists) {
			throw new IllegalOperationException(String.format("A lesson activity already exists for this class on %s.",
					createLessonActivityDTO.getDate()));
		}

		LessonActivity lessonActivity = LessonActivity.builder()
			.scheduledSlot(scheduledSlot)
			.date(createLessonActivityDTO.getDate())
			.description(createLessonActivityDTO.getDescription())
			.build();

		lessonActivityRepository.save(lessonActivity);
	}

	/**
	 * Retrieves lesson activities for the teacher. If a specific slot ID is provided, it
	 * filters the results to that slot only. If the slot ID is null, it fetches ALL
	 * lesson activities across all classes assigned to the teacher. Enforces ownership
	 * validation when a specific slot is requested.
	 * @param teacherUsername the system username of the teacher fetching the records
	 * @param slotId the identifier of the scheduled slot to filter by (optional, can be
	 * null)
	 * @return an ordered list of {@link LessonActivityDTO} (newest/future first)
	 * @throws ResourceNotFoundException if the teacher or the requested slot is missing
	 * from the database
	 * @throws UnauthorizedActionException if security verification fails (teacher does
	 * not own the requested slot)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LessonActivityDTO> getLessonActivities(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		List<LessonActivity> activities;

		// Dynamic Filtering
		if (slotId != null) {

			// Specific Slot Requested
			ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(slotId)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Scheduled slot with ID %d not found.", slotId)));

			if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
				throw new UnauthorizedActionException(
						"You do not have permission to view lesson activities for a class you do not teach.");
			}
			activities = lessonActivityRepository.findAllByScheduledSlotIdWithDetailsOrderByDateDesc(slotId);
		}
		else {

			// No Slot Requested. Bring everything for this teacher
			activities = lessonActivityRepository.findAllByTeacherIdWithDetailsOrderByDateDesc(teacher.getId());
		}

		return activities.stream()
			.map(activity -> LessonActivityDTO.builder()
				.id(activity.getId())
				.slotId(activity.getScheduledSlot().getId())
				.courseTitle(activity.getScheduledSlot().getCourse().getTitle())
				.date(activity.getDate())
				.description(activity.getDescription())
				.build())
			.toList();
	}

	/**
	 * Updates an existing lesson activity. strictly enforces: 1. Teacher existence and
	 * ownership of the related class slot. 2. Frozen historical logs constraint (stored
	 * activity date must not be older than 14 days). Note: The activity date itself is
	 * immutable and cannot be modified.
	 * @param teacherUsername the username of the teacher requesting the update
	 * @param activityId the ID of the lesson activity to update
	 * @param updateLessonActivityDTO the updated data payload containing the new
	 * description
	 * @throws ResourceNotFoundException if the activity is missing
	 * @throws UnauthorizedActionException if the teacher doesn't teach this class
	 * @throws IllegalOperationException if the historical state is locked (frozen)
	 */
	@Override
	@Transactional
	public void updateLessonActivity(String teacherUsername, Long activityId,
			UpdateLessonActivityDTO updateLessonActivityDTO)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		LessonActivity lessonActivity = lessonActivityRepository.findById(activityId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Lesson activity with ID %d not found.", activityId)));

		ScheduledSlot scheduledSlot = lessonActivity.getScheduledSlot();

		// Security Validation - Ensure the teacher owns this class
		if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to modify a lesson activity for a class you do not teach.");
		}

		// Check if the CURRENT stored lesson activity is already frozen
		LocalDate today = LocalDate.now();
		LocalDate pastLimit = today.minusDays(14);

		if (lessonActivity.getDate().isBefore(pastLimit)) {
			throw new IllegalOperationException(
					"This lesson activity is older than 14 days and its historical state is frozen.");
		}

		// Apply updates
		lessonActivity.setDescription(updateLessonActivityDTO.getDescription());

		lessonActivityRepository.save(lessonActivity);
	}

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
	@Override
	@Transactional
	public void deleteLessonActivity(String teacherUsername, Long activityId)
			throws ResourceNotFoundException, UnauthorizedActionException, IllegalOperationException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		LessonActivity lessonActivity = lessonActivityRepository.findById(activityId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Lesson activity with ID %d not found.", activityId)));

		ScheduledSlot scheduledSlot = lessonActivity.getScheduledSlot();

		// Security Validation
		if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to delete a lesson activity for a class you do not teach.");
		}

		// Strict Historical Validation (The No-Delete-Past Rule)
		LocalDate today = LocalDate.now();
		if (lessonActivity.getDate().isBefore(today)) {
			throw new IllegalOperationException("Cannot delete a lesson activity that has already taken place. "
					+ "Historical records can only be modified (within 14 days), not erased.");
		}

		// Perform Deletion (Only for Today or Future plans)
		lessonActivityRepository.delete(lessonActivity);
	}

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
	@Override
	@Transactional(readOnly = true)
	public List<ActiveStudentDTO> getActiveStudentsForSlot(String teacherUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Teacher teacher = teacherRepository.findByUsername(teacherUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher '%s' not found.", teacherUsername)));

		ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(slotId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Scheduled slot '%d' not found.", slotId)));

		// Security Validation - Ensure the teacher owns this class
		if (!scheduledSlot.getTeacher().getId().equals(teacher.getId())) {
			throw new UnauthorizedActionException(
					"You do not have permission to view students for a class you do not teach.");
		}

		// Fetch active enrollments
		List<Enrollment> activeEnrollments = enrollmentRepository
			.findAllByScheduledSlotIdAndStatus(scheduledSlot.getId(), EnrollmentStatus.ACTIVE);

		return activeEnrollments.stream()
			.map(Enrollment::getStudent)
			.map(EntityMapper.MAPPER::toActiveStudentDTO)
			.toList();
	}

	// Helper Method

	/**
	 * Helper method to validate if a specific scheduled slot is eligible for an absence.
	 * Enforces ownership, day-of-week matching, and the presence of active students.
	 */
	private void validateSlotForAbsence(ScheduledSlot slot, LocalDate date, Long teacherId)
			throws UnauthorizedActionException, IllegalOperationException {

		// Check Ownership: Does this slot belong to the requesting teacher?
		if (!slot.getTeacher().getId().equals(teacherId)) {
			throw new UnauthorizedActionException("You cannot declare an absence for a class you do not teach.");
		}

		// Check Day Match: Does this slot actually happen on the requested date?
		// (e.g., You can't cancel a WEDNESDAY slot on a TUESDAY date)
		if (slot.getDayOfWeek() != date.getDayOfWeek()) {
			throw new IllegalOperationException("The selected slot does not occur on the requested day of the week.");
		}

		// Check for Active Students
		List<Object[]> slotCounts = enrollmentRepository.countEnrolledStudentsPerSlotForTeacher(teacherId,
				List.of(EnrollmentStatus.ACTIVE));

		boolean hasActiveStudents = slotCounts.stream()
			.anyMatch(row -> row[0].equals(slot.getId()) && ((Long) row[1]) > 0);

		if (!hasActiveStudents) {
			throw new IllegalOperationException(
					"Cannot declare an absence for a specific class that has no active enrolled students.");
		}
	}

}

package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.dto.student.StudentScheduleSlotDTO;
import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;
import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.domain.exception.*;
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
import java.util.stream.Collectors;

/**
 * JPA-based implementation of the {@link StudentPersistence} output port. Acts as the
 * primary persistence adapter for all student-centric domain operations. Orchestrates
 * complex, multi-entity transactions including student registration, profile management,
 * class enrollment lifecycles, schedule aggregation, and the retrieval of academic
 * performance metrics (test results and activities).
 */
@Service
@AllArgsConstructor
public class StudentPersistenceImpl implements StudentPersistence {

	private final StudentRepository studentRepository;

	private final ScheduledSlotRepository scheduledSlotRepository;

	private final EnrollmentRepository enrollmentRepository;

	private final TeacherAbsenceRepository teacherAbsenceRepository;

	private final TestResultRepository testResultRepository;

	private final LessonActivityRepository lessonActivityRepository;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Persists a new student entity to the database during the registration process,
	 * setting the initial default values such as role and disabled status.
	 * @param registerStudentDTO the validated student registration data
	 * @param passwordHash the cryptographically hashed password
	 */
	@Override
	@Transactional
	public void saveStudentFromRegister(RegisterStudentDTO registerStudentDTO, String passwordHash) {

		Student student = mapper.toStudentEntityFromRegister(registerStudentDTO);

		student.setPasswordHash(passwordHash);
		student.setEnabled(false);
		student.setRole(Role.STUDENT);

		studentRepository.save(student);
	}

	/**
	 * Retrieves a student by its ID.
	 * @param id the student ID
	 * @return the student DTO
	 */
	@Override
	@Transactional(readOnly = true)
	public StudentDTO findById(long id) {
		return mapper.toStudentDTO(studentRepository.getReferenceById(id));
	}

	/**
	 * Retrieves all students from the database.
	 * @return a list of student DTOs
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentDTO> findAll() {
		return studentRepository.findAll().stream().map(mapper::toStudentDTO).toList();
	}

	/**
	 * Saves a student entity to the database.
	 * @param studentDTO the student data to persist
	 * @return the saved student DTO
	 */
	@Override
	@Transactional
	public StudentDTO save(StudentDTO studentDTO) {
		Student student = mapper.toStudentEntity(studentDTO);

		return mapper.toStudentDTO(studentRepository.save(student));
	}

	/**
	 * Processes a student's enrollment into a specific scheduled slot, enforcing rules
	 * such as duplicate enrollment checks, time-schedule conflicts and slot capacity
	 * limits.
	 * @param username the username of the student requesting enrollment
	 * @param slotId the ID of the scheduled slot to enroll in
	 * @return an {@link EnrollmentResponseDTO} containing the details of the newly
	 * created enrollment
	 * @throws ResourceNotFoundException if the student or the scheduled slot does not
	 * exist
	 * @throws IllegalOperationException if the requested scheduled slot overlaps in time
	 * with another class the student is already enrolled in
	 */
	@Override
	@Transactional
	public EnrollmentResponseDTO enrollStudent(String username, Long slotId)
			throws ResourceNotFoundException, IllegalOperationException {

		Student student = studentRepository.findByUsername(username)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Student '%s' not found.", username)));

		ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(slotId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Scheduled slot '%d' not found.", slotId)));

		// Verify that the student doesn't already have an active or pending enrollment
		// for this slot
		if (enrollmentRepository.existsByStudentIdAndScheduledSlotIdAndStatusNot(student.getId(), slotId,
				EnrollmentStatus.DROPPED)) {
			throw new AlreadyEnrolledException(String.format(
					"You are already enrolled in this class '%s', or you have a pending enrollment/drop request.",
					scheduledSlot.getCourse().getTitle()));
		}

		// Time Overlap Schedule Check : the student is not allowed
		// to be in two different classes at the same time
		boolean hasScheduleConflict = enrollmentRepository.existsOverlappingEnrollmentForStudent(student.getId(),
				scheduledSlot.getDayOfWeek(), scheduledSlot.getStartTime(), scheduledSlot.getEndTime(),
				EnrollmentStatus.DROPPED);

		if (hasScheduleConflict) {
			throw new IllegalOperationException(String.format(
					"You are already enrolled in another class on '%s' that overlaps with the time frame %s - %s.",
					scheduledSlot.getDayOfWeek(), scheduledSlot.getStartTime(), scheduledSlot.getEndTime()));
		}

		// Verify that the scheduled slot has not exceeded its maximum capacity of
		// active/pending students
		int activeCount = enrollmentRepository.countByScheduledSlotIdAndStatusIn(slotId,
				List.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.PENDING_DROP, EnrollmentStatus.PENDING_ENROLL));

		if (activeCount >= scheduledSlot.getCapacity()) {
			throw new CapacityExceededException(String.format("The class '%s' has reached its maximum capacity.",
					scheduledSlot.getCourse().getTitle()));
		}

		Enrollment enrollment = Enrollment.builder()
			.student(student)
			.scheduledSlot(scheduledSlot)
			.enrollmentDate(LocalDate.now())
			.status(EnrollmentStatus.PENDING_ENROLL)
			.build();

		student.addEnrollment(enrollment);
		scheduledSlot.addEnrollment(enrollment);

		enrollmentRepository.save(enrollment);

		return mapper.toEnrollmentResponseDTO(enrollment);
	}

	/**
	 * Retrieves a list of all enrollments associated with a specific student, ordered by
	 * the enrollment date in descending order (most recent first).
	 * @param username the username of the student
	 * @return a list of {@link EnrollmentResponseDTO} representing the student's
	 * enrollments
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EnrollmentResponseDTO> getMyEnrollments(String username, List<EnrollmentStatus> statuses) {

		List<Enrollment> enrollments;

		if (statuses == null || statuses.isEmpty()) {
			enrollments = enrollmentRepository.findAllByStudentUsernameOrderByEnrollmentDateDesc(username);
		}
		else {
			enrollments = enrollmentRepository.findAllByStudentUsernameAndStatusInOrderByEnrollmentDateDesc(username,
					statuses);
		}

		return enrollments.stream().map(mapper::toEnrollmentResponseDTO).toList();
	}

	/**
	 * Processes a student's request to drop an active enrollment. The enrollment's status
	 * is updated to pending drop until reviewed and approved by an administrator.
	 * @param username the username of the student requesting the drop
	 * @param enrollmentId the ID of the enrollment to be dropped
	 * @throws ResourceNotFoundException if the specified enrollment does not exist
	 * @throws UnauthorizedActionException if the student does not own the specified
	 * enrollment
	 * @throws InvalidEnrollmentStateException if the enrollment is not in an active state
	 */
	@Override
	@Transactional
	public void requestEnrollmentDrop(String username, Long enrollmentId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Enrollment '%d' not found.", enrollmentId)));

		// Property Control: Ensure the student actually owns this enrollment
		if (!enrollment.getStudent().getUsername().equals(username)) {
			throw new UnauthorizedActionException("You cannot drop an enrollment that does not belong to you.");
		}

		if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
			throw new InvalidEnrollmentStateException("Only active enrollments can be dropped.");
		}

		enrollment.setStatus(EnrollmentStatus.PENDING_DROP);
		enrollmentRepository.save(enrollment);
	}

	/**
	 * Retrieves the dynamically generated schedule for a student within a specific date
	 * range. This method merges the student's static enrollment data with dynamic
	 * operational data (teacher absences) and educational plans (lesson activities).
	 * @param studentUsername the username of the student requesting their schedule
	 * @param startDate the start date of the requested schedule range
	 * @param endDate the end date of the requested schedule range (inclusive)
	 * @return a chronologically ordered list of {@link StudentDailyScheduleDTO}
	 * representing the daily schedule
	 * @throws ResourceNotFoundException if the requesting student cannot be found
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentDailyScheduleDTO> getStudentSchedule(String studentUsername, LocalDate startDate,
			LocalDate endDate) throws ResourceNotFoundException {

		// Identity Validation
		// Ensure the requesting student exists before proceeding with data retrieval.
		Student student = studentRepository.findByUsername(studentUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Student '%s' not found.", studentUsername)));

		// Baseline Data Retrieval
		// Fetch static enrollments and dynamic absences for the requested period.
		List<ScheduledSlot> activeSlots = scheduledSlotRepository.findAllActiveSlotsForStudent(studentUsername);
		List<TeacherAbsence> absences = teacherAbsenceRepository.findByDateBetween(startDate, endDate);

		// Optimized Lesson Activity Fetching
		// Extract slot IDs to bulk-fetch all relevant activities in a single query.
		List<Long> activeSlotIds = activeSlots.stream().map(ScheduledSlot::getId).toList();
		List<LessonActivity> activitiesInRange = activeSlotIds.isEmpty() ? Collections.emptyList()
				: lessonActivityRepository.findActivitiesForSlotsInDateRange(activeSlotIds, startDate, endDate);

		// Data Indexing for O(1) Lookups
		// Group activities first by Date, then by Slot ID, allowing instant access during
		// the schedule generation loop.
		Map<LocalDate, Map<Long, LessonActivity>> activityLookupMap = activitiesInRange.stream()
			.collect(Collectors.groupingBy(LessonActivity::getDate,
					Collectors.toMap(activity -> activity.getScheduledSlot().getId(), activity -> activity,
							(existing, replacement) -> existing)));

		// Initialize the main list that will hold the final constructed schedule
		List<StudentDailyScheduleDTO> schedule = new ArrayList<>();

		// Schedule Generation Loop
		// Iterate through each date in the range to construct the daily timeline.
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

			DayOfWeek currentDayOfWeek = date.getDayOfWeek();
			List<StudentScheduleSlotDTO> dailySlots = new ArrayList<>();

			// Retrieve activities specifically targeted for the current loop date
			Map<Long, LessonActivity> todayActivities = activityLookupMap.getOrDefault(date, Collections.emptyMap());

			// Slot Matching & Assembly
			// Evaluate each active slot to determine if it occurs on the current day.
			for (ScheduledSlot slot : activeSlots) {

				if (slot.getDayOfWeek() == currentDayOfWeek) {

					// Evaluate dynamic cancellations (e.g., Teacher Absences)
					LocalDate currentDate = date;
					TeacherAbsence matchingAbsence = absences.stream()
						.filter(a -> a.getDate().equals(currentDate)
								&& a.getTeacher().getId().equals(slot.getTeacher().getId()))
						.findFirst()
						.orElse(null);

					String status = "SCHEDULED";
					String cancelReason = null;

					if (matchingAbsence != null) {
						status = "CANCELLED";
						cancelReason = "Teacher absence: " + matchingAbsence.getReason();
					}

					String teacherFullName = slot.getTeacher().getFullName();

					// Attach lesson activity metadata, if an activity is planned for
					// today
					LessonActivity todayActivity = todayActivities.get(slot.getId());
					Long activityId = (todayActivity != null) ? todayActivity.getId() : null;
					String activityDescription = (todayActivity != null) ? todayActivity.getDescription() : null;

					// Construct the final DTO for the matched slot
					StudentScheduleSlotDTO scheduleSlotDTO = StudentScheduleSlotDTO.builder()
						.slotId(slot.getId())
						.courseTitle(slot.getCourse().getTitle())
						.teacherName(teacherFullName)
						.startTime(slot.getStartTime())
						.endTime(slot.getEndTime())
						.classroom(slot.getClassroom())
						.status(status)
						.cancelReason(cancelReason)
						.activityId(activityId)
						.activityDescription(activityDescription)
						.build();

					// Add the constructed slot to today's list
					dailySlots.add(scheduleSlotDTO);
				}
			}

			// Chronological Sorting
			// Order the day's classes by start time (morning to evening) for UI
			// consistency.
			dailySlots.sort(Comparator.comparing(StudentScheduleSlotDTO::getStartTime));

			// Daily Schedule Encapsulation
			StudentDailyScheduleDTO dailySchedule = StudentDailyScheduleDTO.builder()
				.date(date)
				.dayOfWeek(currentDayOfWeek)
				.slots(dailySlots)
				.build();

			schedule.add(dailySchedule);
		}

		return schedule;
	}

	/**
	 * Retrieves all tests assigned to a student, including both graded and ungraded
	 * (pending) tests.
	 * @param username the username of the student requesting their tests
	 * @return a list of {@link StudentTestResponseDTO} representing the student's test
	 * history
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentTestResponseDTO> getStudentTestResults(String username) {

		List<TestResult> testResults = testResultRepository
			.findAllByStudentUsernameWithDetailsOrderByDateDesc(username);

		return testResults.stream().map(testResult -> {
			StudentTestResponseDTO response = new StudentTestResponseDTO();

			response.setTestResultId(testResult.getId());
			response.setCourseName(testResult.getTest().getCourse().getTitle());
			response.setTeacherName(testResult.getTest().getAuthor().getFullName());
			response.setDate(testResult.getTest().getDate());
			response.setDescription(testResult.getTest().getDescription());
			response.setGrade(testResult.getGrade());
			response.setComments(testResult.getComments());

			response.setStatus(testResult.getGrade() == null ? "PENDING" : "GRADED");

			return response;
		}).toList();
	}

	/**
	 * Retrieves upcoming lesson activities for the student. If a slotId is provided, it
	 * returns future activities for that specific class after ensuring the student has an
	 * ACTIVE enrollment. If slotId is null, it returns a consolidated To-Do list of all
	 * upcoming activities across all active classes. Past activities (before today) are
	 * strictly filtered out.
	 * @param studentUsername the username of the authenticated student
	 * @param slotId the optional slot ID to filter by (can be null)
	 * @return an ordered list of {@link StudentLessonActivityDTO} sorted by date
	 * ascending
	 * @throws ResourceNotFoundException if the student is missing from the database
	 * @throws UnauthorizedActionException if the student tries to access a slot they
	 * don't actively participate in
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentLessonActivityDTO> getStudentLessonActivities(String studentUsername, Long slotId)
			throws ResourceNotFoundException, UnauthorizedActionException {

		Student student = studentRepository.findByUsername(studentUsername)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Student '%s' not found.", studentUsername)));

		List<LessonActivity> activities;
		LocalDate today = LocalDate.now();

		if (slotId != null) {
			// Specific class timeline requested
			// Check if the student has an ACTIVE enrollment in this class
			boolean isEnrolled = enrollmentRepository.existsByStudentIdAndScheduledSlotIdAndStatus(student.getId(),
					slotId, EnrollmentStatus.ACTIVE);

			if (!isEnrolled) {
				throw new UnauthorizedActionException("You are not actively enrolled in this class slot.");
			}

			activities = lessonActivityRepository.findUpcomingByStudentActiveEnrollmentAndSlotId(student.getId(),
					slotId, today);
		}
		else {
			// Global timeline requested
			activities = lessonActivityRepository.findUpcomingByStudentActiveEnrollments(student.getId(), today);
		}

		return activities.stream().map(EntityMapper.MAPPER::toStudentLessonActivityDTO).toList();
	}

}

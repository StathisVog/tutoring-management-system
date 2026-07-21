package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.admin.*;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.InvalidEnrollmentStateException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.repository.jpa.entity.*;
import com.education.tutoring.management.repository.jpa.mapper.EntityMapper;
import com.education.tutoring.management.repository.jpa.repository.*;
import com.education.tutoring.management.repository.jpa.repository.specification.LessonActivitySpecification;
import com.education.tutoring.management.repository.jpa.repository.specification.TeacherAbsenceSpecification;
import com.education.tutoring.management.repository.jpa.repository.specification.TestSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA-based implementation of the {@link AdminPersistence} output port. Acts as the
 * persistence adapter orchestrating complex administrative data operations, such as
 * managing student enrollment lifecycles (approvals, rejections, drops) and overseeing
 * system-wide entities using Spring Data repositories within secure transactional
 * boundaries.
 */
@Service
@AllArgsConstructor
public class AdminPersistenceImpl implements AdminPersistence {

	private final EnrollmentRepository enrollmentRepository;

	private final ScheduledSlotRepository scheduledSlotRepository;

	private final LessonActivityRepository lessonActivityRepository;

	private final TestRepository testRepository;

	private final TeacherAbsenceRepository teacherAbsenceRepository;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Retrieves a list of all enrollments that are currently pending administrative
	 * approval. These represent initial student requests to enroll in a scheduled slot.
	 * @return a list of {@link EnrollmentResponseDTO} containing the details of the
	 * pending enrollment requests
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EnrollmentResponseDTO> getPendingEnrollRequests() {

		List<Enrollment> pendingEnrollments = enrollmentRepository.findAllByStatus(EnrollmentStatus.PENDING_ENROLL);

		return pendingEnrollments.stream().map(mapper::toEnrollmentResponseDTO).toList();
	}

	/**
	 * Approves a pending enrollment request, formally admitting the student into the
	 * class. This action transitions the enrollment status from PENDING_ENROLL to ACTIVE.
	 * @param enrollmentId the ID of the enrollment to approve
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found in
	 * the database
	 * @throws InvalidEnrollmentStateException if the enrollment is not currently in a
	 * PENDING_ENROLL state
	 */
	@Override
	@Transactional
	public void approveEnroll(Long enrollmentId) throws ResourceNotFoundException {

		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
			.orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

		if (enrollment.getStatus() != EnrollmentStatus.PENDING_ENROLL) {
			throw new InvalidEnrollmentStateException(
					"Only enrollments with PENDING_ENROLL status can be approved for enrollment.");
		}

		enrollment.setStatus(EnrollmentStatus.ACTIVE);
		enrollmentRepository.save(enrollment);
	}

	/**
	 * Rejects a pending enrollment request. This action permanently deletes the request
	 * from the database, effectively clearing the temporarily held seat in the scheduled
	 * slot.
	 * @param enrollmentId the ID of the enrollment request to reject
	 * @throws ResourceNotFoundException if the specified enrollment cannot be found in
	 * the database
	 * @throws InvalidEnrollmentStateException if the enrollment is not currently in a
	 * PENDING_ENROLL state
	 */
	@Override
	@Transactional
	public void rejectEnroll(Long enrollmentId) throws ResourceNotFoundException {

		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Enrollment '%d' not found.", enrollmentId)));

		// Only enrollments in PENDING_ENROLL can be rejected
		if (enrollment.getStatus() != EnrollmentStatus.PENDING_ENROLL) {
			throw new InvalidEnrollmentStateException("Only enrollments with PENDING_ENROLL status can be rejected.");
		}

		enrollmentRepository.delete(enrollment);
	}

	/**
	 * Retrieves a list of all enrollments that are currently flagged with a pending drop
	 * status. These represent student requests to unenroll that are awaiting
	 * administrative approval.
	 * @return a list of {@link EnrollmentResponseDTO} containing the details of the
	 * pending drop requests
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EnrollmentResponseDTO> getPendingDropRequests() {

		List<Enrollment> pendingEnrollments = enrollmentRepository.findAllByStatus(EnrollmentStatus.PENDING_DROP);

		return pendingEnrollments.stream().map(mapper::toEnrollmentResponseDTO).toList();
	}

	/**
	 * Processes the approval of a pending enrollment drop request. This action physically
	 * deletes the enrollment from the database, thereby freeing up capacity for the
	 * scheduled slot.
	 * @param enrollmentId the ID of the enrollment to approve for dropping
	 * @throws ResourceNotFoundException if the specified enrollment does not exist
	 * @throws InvalidEnrollmentStateException if the enrollment is not currently in a
	 * PENDING_DROP state
	 */
	@Override
	@Transactional
	public void approveDrop(Long enrollmentId) throws ResourceNotFoundException {

		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Enrollment '%d' not found.", enrollmentId)));

		// Safety check: Only enrollments in PENDING_DROP should be processed here
		if (enrollment.getStatus() != EnrollmentStatus.PENDING_DROP) {
			throw new InvalidEnrollmentStateException(
					"Only enrollments with PENDING_DROP status can be approved for removal.");
		}

		enrollment.setStatus(EnrollmentStatus.DROPPED);
		enrollmentRepository.save(enrollment);
	}

	/**
	 * Retrieves the class roster for a specific scheduled slot. This method fetches all
	 * enrollments (and their associated student details eagerly) for the given slot,
	 * deliberately excluding students whose enrollment status is 'DROPPED'.
	 * @param scheduledSlotId the ID of the scheduled slot
	 * @return an alphabetically ordered list of {@link EnrolledStudentDTO}
	 * @throws ResourceNotFoundException if the scheduled slot cannot be found in the
	 * database
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EnrolledStudentDTO> getEnrolledStudentsBySlot(Long scheduledSlotId) throws ResourceNotFoundException {

		boolean slotExists = scheduledSlotRepository.existsById(scheduledSlotId);
		if (!slotExists) {
			throw new ResourceNotFoundException(String.format("Scheduled slot with ID %d not found.", scheduledSlotId));
		}

		List<Enrollment> enrollments = enrollmentRepository
			.findAllByScheduledSlotIdAndStatusNotWithStudentOrderByStudentName(scheduledSlotId,
					EnrollmentStatus.DROPPED);

		return enrollments.stream()
			.map(enrollment -> EnrolledStudentDTO.builder()
				.studentId(enrollment.getStudent().getId())
				.fullName(enrollment.getStudent().getFullName())
				.email(enrollment.getStudent().getEmail())
				.username(enrollment.getStudent().getUsername())
				.address(enrollment.getStudent().getAddress())
				.enrollmentDate(enrollment.getEnrollmentDate())
				.status(enrollment.getStatus().name())
				.build())
			.toList();
	}

	/**
	 * Retrieves a comprehensive, dynamically filtered list of lesson activities for
	 * administrative auditing. All filtering parameters are optional and stack
	 * dynamically to build the search criteria using Specifications.
	 * @param teacherId optional filter by teacher
	 * @param courseId optional filter by course
	 * @param slotId optional filter by specific scheduled slot
	 * @param startDate optional lower bound for the activity date
	 * @param endDate optional upper bound for the activity date
	 * @return a chronologically ordered list of {@link AdminLessonActivityDTO}
	 * representing the filtered activities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AdminLessonActivityDTO> getLessonActivities(Long teacherId, Long courseId, Long slotId,
			LocalDate startDate, LocalDate endDate) {

		List<LessonActivity> activities = lessonActivityRepository
			.findAll(LessonActivitySpecification.withDynamicFilters(teacherId, courseId, slotId, startDate, endDate));

		return activities.stream().map(mapper::toAdminLessonActivityDTO).toList();
	}

	/**
	 * Forcefully updates an existing lesson activity, bypassing the standard 14-day
	 * timeframe restrictions imposed on teachers. As an administrative action, it
	 * strictly enforces structural integrity constraints to ensure the new date matches
	 * the day of the week of the scheduled slot, and that no duplicate activity exists on
	 * the target date.
	 * @param activityId the unique identifier of the lesson activity to be updated
	 * @param adminUpdateLessonActivityDTO the data transfer object containing the
	 * modified date and description
	 * @throws ResourceNotFoundException if the specified lesson activity cannot be found
	 * in the database
	 * @throws IllegalOperationException if the new date violates structural constraints
	 * (e.g., mismatching day of week or duplicate entry)
	 */
	@Override
	@Transactional
	public void updateLessonActivity(Long activityId, AdminUpdateLessonActivityDTO adminUpdateLessonActivityDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		LessonActivity lessonActivity = lessonActivityRepository.findById(activityId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Lesson activity '%d' not found.", activityId)));

		ScheduledSlot scheduledSlot = lessonActivity.getScheduledSlot();
		LocalDate newDate = adminUpdateLessonActivityDTO.getDate();

		// Structural Constraint: Day of Week MUST match
		if (newDate.getDayOfWeek() != scheduledSlot.getDayOfWeek()) {
			throw new IllegalOperationException(
					String.format("Date mismatch. The class is scheduled on a %s, but the provided date is a %s.",
							scheduledSlot.getDayOfWeek(), newDate.getDayOfWeek()));
		}

		// Structural Constraint: No duplicate activities on the same day for the same
		// slot
		// Don't throw an error if the Admin keeps the same date but changes the
		// description.
		boolean duplicateExists = lessonActivityRepository.existsByScheduledSlotIdAndDateAndIdNot(scheduledSlot.getId(),
				newDate, activityId);

		if (duplicateExists) {
			throw new IllegalOperationException(
					String.format("A lesson activity already exists for this slot on %s.", newDate));
		}

		lessonActivity.setDate(newDate);
		lessonActivity.setDescription(adminUpdateLessonActivityDTO.getDescription());

		lessonActivityRepository.save(lessonActivity);
	}

	/**
	 * Forcefully deletes a lesson activity from the system. This administrative action
	 * completely removes the record, bypassing any timeframe restrictions.
	 * @param activityId the unique identifier of the lesson activity to delete
	 * @throws ResourceNotFoundException if the lesson activity does not exist in the
	 * database
	 */
	@Override
	@Transactional
	public void deleteLessonActivity(Long activityId) throws ResourceNotFoundException {

		LessonActivity lessonActivity = lessonActivityRepository.findById(activityId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Lesson activity '%d' not found.", activityId)));

		lessonActivityRepository.delete(lessonActivity);
	}

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
	@Override
	@Transactional(readOnly = true)
	public List<AdminTestDTO> getTests(Long teacherId, Long courseId, LocalDate startDate, LocalDate endDate) {

		// Fetch data using dynamic Specification
		List<Test> tests = testRepository
			.findAll(TestSpecification.withDynamicFilters(teacherId, courseId, startDate, endDate));

		return tests.stream().map(mapper::toAdminTestDTO).toList();
	}

	/**
	 * Forcefully updates a test's metadata and optionally its nested student results.
	 * Implements a "Validate First, Mutate Later" strategy to ensure data integrity and
	 * rolls back the transaction entirely if any structural or security rule is violated.
	 * @param testId the ID of the test to update
	 * @param adminUpdateTestDTO the payload containing the new metadata and optional
	 * grade changes
	 * @throws ResourceNotFoundException if the test is not found
	 * @throws IllegalOperationException if date constraints are violated or result IDs
	 * mismatch
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTest(Long testId, AdminUpdateTestDTO adminUpdateTestDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		Test test = testRepository.findById(testId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Test '%d' not found.", testId)));

		// Structural Constraint: Unique Course + Date combination
		boolean duplicateExists = testRepository.existsByCourseIdAndDateAndIdNot(test.getCourse().getId(),
				adminUpdateTestDTO.getDate(), testId);

		if (duplicateExists) {
			throw new IllegalOperationException(String.format("A test already exists for course '%s' on %s.",
					test.getCourse().getTitle(), adminUpdateTestDTO.getDate()));
		}

		// Validation: Ensure data integrity BEFORE modifying the entity
		if (adminUpdateTestDTO.getResultsToUpdate() != null && !adminUpdateTestDTO.getResultsToUpdate().isEmpty()) {
			for (AdminUpdateTestResultDTO resultUpdate : adminUpdateTestDTO.getResultsToUpdate()) {

				boolean belongsToTest = test.getTestResults()
					.stream()
					.anyMatch(tr -> tr.getId().equals(resultUpdate.getTestResultId()));

				if (!belongsToTest) {
					throw new IllegalOperationException(
							String.format("Test result '%d' does not belong to test '%s'. Update aborted.",
									resultUpdate.getTestResultId(), test.getDescription()));
				}
			}
		}

		// Mutation: Apply updates to parent metadata
		test.setDate(adminUpdateTestDTO.getDate());
		test.setDescription(adminUpdateTestDTO.getDescription());

		// Mutation: Apply updates to nested Test Results
		if (adminUpdateTestDTO.getResultsToUpdate() != null && !adminUpdateTestDTO.getResultsToUpdate().isEmpty()) {

			for (AdminUpdateTestResultDTO resultUpdate : adminUpdateTestDTO.getResultsToUpdate()) {

				// We have already guaranteed existence during the Validation Phase
				TestResult targetResult = test.getTestResults()
					.stream()
					.filter(tr -> tr.getId().equals(resultUpdate.getTestResultId()))
					.findFirst()
					.orElseThrow(() -> new IllegalOperationException(String.format(
							"Critical state mismatch: Test result '%d' could not be retrieved from the managed transaction context.",
							resultUpdate.getTestResultId())));

				// Apply the updates to the managed entity
				targetResult.setGrade(resultUpdate.getGrade());
				targetResult.setComments(resultUpdate.getComments());
			}
		}

		testRepository.save(test);
	}

	/**
	 * Forcefully deletes a test and all its associated student results from the system.
	 * This administrative action permanently removes the records, utilizing database
	 * cascading to ensure no orphaned test results are left behind.
	 * @param testId the unique identifier of the test to delete
	 * @throws ResourceNotFoundException if the test does not exist in the database
	 */
	@Override
	@Transactional
	public void deleteTest(Long testId) throws ResourceNotFoundException {

		Test test = testRepository.findById(testId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Test '%d' not found.", testId)));

		testRepository.delete(test);
	}

	/**
	 * Forcefully removes a specific student's test result from a parent test. By severing
	 * the bidirectional relationship within the managed entity, Hibernate's orphan
	 * removal mechanism is triggered to permanently delete the result record.
	 * @param testId the unique identifier of the parent test
	 * @param testResultId the unique identifier of the nested test result to delete
	 * @throws ResourceNotFoundException if the parent test is not found, or if the
	 * specified test result does not belong to it
	 */
	@Override
	@Transactional
	public void deleteTestResult(Long testId, Long testResultId) throws ResourceNotFoundException {

		Test test = testRepository.findById(testId)
			.orElseThrow(() -> new ResourceNotFoundException(String.format("Test '%d' not found.", testId)));

		TestResult targetResult = test.getTestResults()
			.stream()
			.filter(tr -> tr.getId().equals(testResultId))
			.findFirst()
			.orElseThrow(() -> new ResourceNotFoundException(String
				.format("Test result '%d' was not found within test '%s'.", testResultId, test.getDescription())));

		test.removeTestResult(targetResult);

		testRepository.save(test);
	}

	/**
	 * Retrieves a comprehensively filtered list of teacher absences. Designed for
	 * administrative oversight, allowing multi-layered dynamic filtering.
	 * @param teacherId optional filter by the teacher declaring the absence
	 * @param slotId optional filter by a specific scheduled slot
	 * @param startDate optional lower bound for the absence date
	 * @param endDate optional upper bound for the absence date
	 * @return a chronologically ordered list of {@link AdminTeacherAbsenceDTO} matching
	 * the criteria
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AdminTeacherAbsenceDTO> getTeacherAbsences(Long teacherId, Long slotId, LocalDate startDate,
			LocalDate endDate) {

		List<TeacherAbsence> absences = teacherAbsenceRepository
			.findAll(TeacherAbsenceSpecification.withDynamicFilters(teacherId, slotId, startDate, endDate));

		return absences.stream().map(mapper::toAdminTeacherAbsenceDTO).toList();
	}

	/**
	 * Forcefully updates a teacher's absence record. Bypasses time restrictions (allows
	 * past dates) but enforces structural integrity, including hybrid logic conversions
	 * between full-day and slot-specific absences.
	 * @param absenceId the unique identifier of the teacher absence to update
	 * @param updateDTO the payload containing the new teacher absence details
	 * @throws ResourceNotFoundException if the teacher absence or target slot cannot be
	 * found in the database
	 * @throws IllegalOperationException if date constraints or uniqueness rules
	 * (duplicate absence) are violated
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTeacherAbsence(Long absenceId, AdminUpdateTeacherAbsenceDTO updateDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		TeacherAbsence teacherAbsence = teacherAbsenceRepository.findById(absenceId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher absence '%d' not found.", absenceId)));

		Teacher teacher = teacherAbsence.getTeacher();
		LocalDate newDate = updateDTO.getDate();
		Long newSlotId = updateDTO.getSlotId();

		ScheduledSlot newSlot = null;

		// Validation: Hybrid Logic and Structural Integrity
		if (newSlotId != null) {
			// Case A: Converting to or Updating a Slot-Specific Absence
			newSlot = scheduledSlotRepository.findById(newSlotId)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Scheduled slot '%d' not found.", newSlotId)));

			if (!newSlot.getTeacher().getId().equals(teacher.getId())) {
				throw new IllegalOperationException(
						"Cannot assign an absence to a slot that belongs to a different teacher.");
			}

			if (newSlot.getDayOfWeek() != newDate.getDayOfWeek()) {
				throw new IllegalOperationException(String.format(
						"Day mismatch: You selected a slot scheduled for %s, but the chosen absence date falls on a %s. The days must match.",
						newSlot.getDayOfWeek(), newDate.getDayOfWeek()));
			}

			if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotIdAndIdNot(teacher.getId(), newDate,
					newSlotId, absenceId)) {
				throw new IllegalOperationException(
						"A duplicate absence already exists for this specific slot and date.");
			}
		}
		else {
			// Case B: Converting to or Updating a Full-Day Absence
			if (!scheduledSlotRepository.existsByTeacherIdAndDayOfWeek(teacher.getId(), newDate.getDayOfWeek())) {
				throw new IllegalOperationException(
						"Cannot declare a full-day absence on a day the teacher has no scheduled classes.");
			}

			if (teacherAbsenceRepository.existsByTeacherIdAndDateAndScheduledSlotIsNullAndIdNot(teacher.getId(),
					newDate, absenceId)) {
				throw new IllegalOperationException("A duplicate full-day absence already exists for this date.");
			}
		}

		// Mutation: Apply verified updates to the Managed Entity
		teacherAbsence.setDate(newDate);
		teacherAbsence.setReason(updateDTO.getReason());
		teacherAbsence.setScheduledSlot(newSlot);

		teacherAbsenceRepository.save(teacherAbsence);
	}

	/**
	 * Forcefully deletes a teacher's absence record from the system. This administrative
	 * action permanently removes the record, freeing up the schedule and correcting
	 * attendance miscalculations.
	 * @param absenceId the unique identifier of the teacher absence to delete
	 * @throws ResourceNotFoundException if the teacher absence does not exist in the
	 * database
	 */
	@Override
	@Transactional
	public void deleteTeacherAbsence(Long absenceId) throws ResourceNotFoundException {

		TeacherAbsence absence = teacherAbsenceRepository.findById(absenceId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Teacher absence '%d' not found.", absenceId)));

		teacherAbsenceRepository.delete(absence);
	}

}

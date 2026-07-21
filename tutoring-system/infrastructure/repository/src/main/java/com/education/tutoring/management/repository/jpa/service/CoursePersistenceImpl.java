package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.out.CoursePersistence;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.TeacherNotAssignedToCourseException;
import com.education.tutoring.management.repository.jpa.entity.Course;
import com.education.tutoring.management.repository.jpa.entity.ScheduledSlot;
import com.education.tutoring.management.repository.jpa.entity.Teacher;
import com.education.tutoring.management.repository.jpa.mapper.EntityMapper;
import com.education.tutoring.management.repository.jpa.repository.CourseRepository;
import com.education.tutoring.management.repository.jpa.repository.ScheduledSlotRepository;
import com.education.tutoring.management.repository.jpa.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of the {@link CoursePersistence} output port. Acts as the
 * persistence adapter responsible for managing the educational core domain. It handles
 * the creation and lifecycle of courses, orchestrates teacher-to-course assignments, and
 * oversees the management of scheduled class slots within secure transactional
 * boundaries.
 */
@Service
@AllArgsConstructor
public class CoursePersistenceImpl implements CoursePersistence {

	private final CourseRepository courseRepository;

	private final TeacherRepository teacherRepository;

	private final ScheduledSlotRepository scheduledSlotRepository;

	private final ScheduledSlotAvailabilityCalculator scheduledSlotAvailabilityCalculator;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Persists the given course data to the database.
	 * @param courseDTO the course data to persist
	 * @return the saved {@link CourseDTO}
	 */
	@Override
	@Transactional
	public CourseDTO save(CourseDTO courseDTO) {

		return mapper.toCourseDTO(courseRepository.save(mapper.toCourseEntity(courseDTO)));
	}

	/**
	 * Checks the database for the existence of a course by its title.
	 * @param title the course title to check
	 * @return true if the course exists, false otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsByTitle(String title) {
		return courseRepository.existsByTitle(title);
	}

	/**
	 * Fetches courses from the database, applying a title filter if provided.
	 * @param title the title to filter by, or null/empty for no filtering
	 * @return a list of fetched {@link CourseDTO}s
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CourseDTO> findAllCourses(String title, Boolean active) {

		String searchTitle = (title == null || title.trim().isEmpty()) ? null : title.trim();

		return courseRepository.findCoursesWithFilters(searchTitle, active).stream().map(mapper::toCourseDTO).toList();
	}

	/**
	 * Fetches a course from the database by its unique identifier.
	 * @param id the unique identifier of the course
	 * @return an {@link Optional} containing the fetched {@link CourseDTO}, or empty if
	 * not found
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CourseDTO> findById(Long id) {
		return courseRepository.findById(id).map(mapper::toCourseDTO);
	}

	/**
	 * Removes the course with the specified ID from the database.
	 * @param id the unique identifier of the course to delete
	 */
	@Override
	@Transactional
	public void deleteById(Long id) {
		courseRepository.deleteById(id);
	}

	/**
	 * Links a teacher to a course using their respective identifiers.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if entities are missing
	 */
	@Override
	@Transactional
	public void assignTeacher(Long courseId, Long teacherId) throws ResourceNotFoundException {

		Course course = courseRepository.findById(courseId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Course with ID '%d' was not found.", courseId)));

		Teacher teacher = teacherRepository.findById(teacherId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Teacher with ID '%d' was not found.", teacherId)));

		// synchronizes both sides
		course.addTeacher(teacher);

		courseRepository.save(course);
	}

	/**
	 * Removes the link between a teacher and a course. Prevents removal if the teacher
	 * still has scheduled classes for this course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if entities are missing
	 * @throws IllegalOperationException if the teacher has active scheduled slots for the
	 * course
	 */
	@Override
	@Transactional
	public void unassignTeacher(Long courseId, Long teacherId)
			throws ResourceNotFoundException, IllegalOperationException {

		Course course = courseRepository.findById(courseId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Course with ID '%d' was not found.", courseId)));

		Teacher teacher = teacherRepository.findById(teacherId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Teacher with ID '%d' was not found.", teacherId)));

		// Prevent unassign if slots exist
		if (scheduledSlotRepository.existsByCourseIdAndTeacherId(courseId, teacherId)) {
			throw new IllegalOperationException(
					"Cannot remove the teacher from the course because they still have scheduled classes. "
							+ "Please reassign or delete the scheduled classes first.");
		}

		// synchronizes both sides
		course.removeTeacher(teacher);

		courseRepository.save(course);
	}

	/**
	 * Retrieves a comprehensive, flattened list of all teacher-to-course assignments
	 * across the system. Iterates through teachers and their initialized courses to build
	 * individual assignment records.
	 * @return a list of {@link CourseAssignmentDTO} representing every active course
	 * assignment
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CourseAssignmentDTO> getAllCourseAssignments() {

		List<Teacher> teachersWithCourses = teacherRepository.findAllWithCourses();

		List<CourseAssignmentDTO> assignments = new ArrayList<>();

		for (Teacher teacher : teachersWithCourses) {
			for (Course course : teacher.getCourses()) {
				assignments.add(new CourseAssignmentDTO(course.getId(), course.getTitle(), teacher.getId(),
						teacher.getFullName(), teacher.getSpecialty()));
			}
		}

		return assignments;
	}

	/**
	 * Retrieves all teachers who are currently assigned to a specific course.
	 * @param courseId the ID of the course to check
	 * @return a list of {@link AssignedTeacherDTO} representing the teachers assigned to
	 * the course
	 * @throws ResourceNotFoundException if the specified course does not exist in the
	 * database
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AssignedTeacherDTO> getAssignedTeachersByCourseId(Long courseId) throws ResourceNotFoundException {

		if (!courseRepository.existsById(courseId)) {
			throw new ResourceNotFoundException(String.format("Course with ID '%d' was not found.", courseId));
		}

		return teacherRepository.findAllByCoursesId(courseId).stream().map(mapper::toAssignedTeacherDTO).toList();
	}

	/**
	 * Creates and persists a new scheduled slot for a specific course. Verifies that the
	 * course and teacher exist, the teacher is assigned to the course, and guarantees
	 * that neither the Teacher nor the Classroom are double-booked (overlapping times) on
	 * the given day.
	 * @param courseId the ID of the course to which the slot will be added
	 * @param scheduledSlotDTO the payload containing the slot details (day, time,
	 * capacity, etc.)
	 * @return the newly created {@link ScheduledSlotDTO} containing its generated
	 * database ID
	 * @throws ResourceNotFoundException if the course or teacher is not found in the
	 * system
	 * @throws TeacherNotAssignedToCourseException if the specified teacher is not
	 * assigned to the given course
	 * @throws IllegalOperationException if there is a time overlap conflict for either
	 * the teacher or the classroom
	 */
	@Override
	@Transactional
	public ScheduledSlotDTO createScheduledSlot(Long courseId, ScheduledSlotDTO scheduledSlotDTO)
			throws ResourceNotFoundException, TeacherNotAssignedToCourseException, IllegalOperationException {

		Course course = courseRepository.findById(courseId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Course '%s' was not found.", scheduledSlotDTO.getCourseTitle())));

		Teacher teacher = teacherRepository.findById(scheduledSlotDTO.getTeacherId())
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Teacher '%s' was not found.", scheduledSlotDTO.getTeacherName())));

		// Check if the teacher is assigned to this course
		if (!course.getTeachers().contains(teacher)) {
			throw new TeacherNotAssignedToCourseException(String.format("Teacher '%s' is not assigned to course '%s'.",
					teacher.getFullName(), course.getTitle()));
		}

		LocalTime newStartTime = scheduledSlotDTO.getStartTime();
		LocalTime newEndTime = scheduledSlotDTO.getEndTime();
		DayOfWeek day = scheduledSlotDTO.getDayOfWeek();

		// Teacher Overlap Validation - Prevents the teacher from being in 2 classes at
		// once
		boolean teacherOverlap = scheduledSlotRepository.existsOverlappingSlotForTeacher(teacher.getId(), day,
				newStartTime, newEndTime);
		if (teacherOverlap) {
			throw new IllegalOperationException(String.format(
					"Teacher '%s' is already scheduled to teach another class during this time frame on '%s'.",
					teacher.getFullName(), day));
		}

		// Classroom Overlap Validation - Prevents creating a class on the same day/time
		// in the same room
		String requestedClassroom = scheduledSlotDTO.getClassroom();
		String formatClassroom = null;

		if (requestedClassroom != null && !requestedClassroom.trim().isEmpty()) {

			// Format for Saving
			formatClassroom = requestedClassroom.trim().replaceAll("\\s+", " ").toUpperCase();

			// Data Sanitization - Format for DB search
			String normalizedClassroom = formatClassroom.replaceAll("\\s+", "");

			boolean classroomOverlap = scheduledSlotRepository.existsOverlappingSlotInClassroom(normalizedClassroom,
					day, newStartTime, newEndTime);

			if (classroomOverlap) {
				throw new IllegalOperationException(String.format(
						"Classroom '%s' is already scheduled for another lesson during this time frame on '%s'.",
						formatClassroom, day));
			}
		}

		ScheduledSlot scheduledSlot = mapper.toScheduledSlotEntity(scheduledSlotDTO);

		// if null then online lesson
		scheduledSlot.setClassroom(formatClassroom);

		scheduledSlot.setTeacher(teacher);
		course.addScheduledSlot(scheduledSlot);

		return mapper.toScheduledSlotDTO(scheduledSlotRepository.save(scheduledSlot));
	}

	/**
	 * Retrieves all scheduled class slots for a specific course, ordered chronologically
	 * by day of week and start time. The returned slots are enriched with real-time seat
	 * availability calculations.
	 * @param courseId the ID of the course
	 * @return a list of enriched {@link ScheduledSlotDTO} representing the course's
	 * timetable
	 * @throws ResourceNotFoundException if the specified course does not exist in the
	 * database
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ScheduledSlotDTO> getScheduledSlotsByCourseId(Long courseId) throws ResourceNotFoundException {

		if (!courseRepository.existsById(courseId)) {
			throw new ResourceNotFoundException(String.format("Course with ID '%d' was not found.", courseId));
		}

		List<ScheduledSlot> slots = scheduledSlotRepository.findAllByCourseIdOrderByDayOfWeekAscStartTimeAsc(courseId);

		return slots.stream().map(scheduledSlotAvailabilityCalculator::toEnrichedDto).toList();
	}

	/**
	 * Permanently deletes a scheduled slot from a specific course. Ensures bidirectional
	 * entity synchronization by removing the slot from the course's internal collection
	 * prior to deletion.
	 * @param courseId the ID of the course owning the slot
	 * @param slotId the ID of the scheduled slot to be deleted
	 * @throws ResourceNotFoundException if the course cannot be found, or if the
	 * specified slot does not exist or does not belong to the given course
	 */
	@Override
	@Transactional
	public void deleteScheduledSlot(Long courseId, Long slotId) throws ResourceNotFoundException {

		Course course = courseRepository.findById(courseId)
			.orElseThrow(
					() -> new ResourceNotFoundException(String.format("Course with ID '%d' was not found.", courseId)));

		ScheduledSlot scheduledSlot = scheduledSlotRepository.findByIdAndCourseId(slotId, courseId)
			.orElseThrow(() -> new ResourceNotFoundException(
					String.format("Scheduled slot with ID '%d' for course ID '%d' was not found.", slotId, courseId)));

		course.removeScheduledSlot(scheduledSlot);

		scheduledSlotRepository.delete(scheduledSlot);
	}

}

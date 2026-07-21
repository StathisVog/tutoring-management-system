package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.TeacherNotAssignedToCourseException;

import java.util.List;
import java.util.Optional;

/**
 * Interface for course-related database operations.
 */
public interface CoursePersistence {

	/**
	 * Saves a new or existing course to the database.
	 * @param courseDTO the course data to persist
	 * @return the saved course DTO
	 */
	CourseDTO save(CourseDTO courseDTO);

	/**
	 * Checks if a course with the given title already exists.
	 * @param title the course title to check
	 * @return true if the course exists, false otherwise
	 */
	boolean existsByTitle(String title);

	/**
	 * Retrieves a list of all courses, optionally filtered by title and active status.
	 * @param title the title to filter by, or null/empty for all courses
	 * @param active the active status to filter by, or null for both active and inactive
	 * @return a list of matching {@link CourseDTO}s
	 */
	List<CourseDTO> findAllCourses(String title, Boolean active);

	/**
	 * Retrieves a course by its unique identifier.
	 * @param id the unique identifier of the course
	 * @return an {@link Optional} containing the {@link CourseDTO} if found, or empty
	 */
	Optional<CourseDTO> findById(Long id);

	/**
	 * Deletes a course from the database by its unique identifier.
	 * @param id the unique identifier of the course to delete
	 */
	void deleteById(Long id);

	/**
	 * Assigns a teacher to a specific course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if either the course or the teacher is not found
	 */
	void assignTeacher(Long courseId, Long teacherId) throws ResourceNotFoundException;

	/**
	 * Unassigns a teacher from a specific course.
	 * @param courseId the unique identifier of the course
	 * @param teacherId the unique identifier of the teacher
	 * @throws ResourceNotFoundException if either the course or the teacher is not found
	 */
	void unassignTeacher(Long courseId, Long teacherId) throws ResourceNotFoundException, IllegalOperationException;

	/**
	 * Retrieves a flat list of all course-to-teacher assignments in the system.
	 * @return a list of {@link CourseAssignmentDTO} representing the assignments
	 */
	List<CourseAssignmentDTO> getAllCourseAssignments();

	/**
	 * Retrieves all teachers assigned to a specific course.
	 * @param courseId the ID of the course
	 * @return a list of {@link AssignedTeacherDTO} representing the assigned teachers
	 * @throws ResourceNotFoundException if the specified course is not found
	 */
	List<AssignedTeacherDTO> getAssignedTeachersByCourseId(Long courseId) throws ResourceNotFoundException;

	/**
	 * Creates and saves a new scheduled slot for a specific course.
	 * @param courseId the ID of the course
	 * @param scheduledSlotDTO the payload containing the slot details
	 * @return the newly created {@link ScheduledSlotDTO} with its generated ID
	 * @throws ResourceNotFoundException if the course or teacher is not found
	 * @throws TeacherNotAssignedToCourseException if the teacher is not assigned to the
	 * course
	 * @throws IllegalOperationException if there is a time overlap conflict for either
	 * the teacher or the classroom
	 */
	ScheduledSlotDTO createScheduledSlot(Long courseId, ScheduledSlotDTO scheduledSlotDTO)
			throws ResourceNotFoundException, TeacherNotAssignedToCourseException, IllegalOperationException;

	/**
	 * Retrieves all scheduled slots associated with a specific course.
	 * @param courseId the ID of the course
	 * @return a list of {@link ScheduledSlotDTO} representing the course's schedule
	 * @throws ResourceNotFoundException if the course is not found
	 */
	List<ScheduledSlotDTO> getScheduledSlotsByCourseId(Long courseId) throws ResourceNotFoundException;

	/**
	 * Deletes a specific scheduled slot associated with a given course.
	 * @param courseId the ID of the course
	 * @param slotId the ID of the scheduled slot to be deleted
	 * @throws ResourceNotFoundException if the course is not found, or if the slot does
	 * not exist or does not belong to the specified course
	 */
	void deleteScheduledSlot(Long courseId, Long slotId) throws ResourceNotFoundException;

}

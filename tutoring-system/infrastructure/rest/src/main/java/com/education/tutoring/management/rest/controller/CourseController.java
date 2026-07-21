package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.in.course.*;
import com.education.tutoring.management.domain.exception.*;
import com.education.tutoring.management.rest.adapter.CourseResourceMapper;
import com.education.tutoring.management.rest.adapter.ScheduledSlotResourceMapper;
import com.education.tutoring.management.rest.model.*;
import com.education.tutoring.management.rest.model.course.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller acting as the primary driving adapter for curriculum and course
 * management. Orchestrates domain use cases handling the complete lifecycle of
 * educational courses, including structural updates, dynamic teacher-to-course
 * assignments, and the configuration of scheduled class slots.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
@Tag(name = "Course API", description = "Endpoints for managing educational courses")
public class CourseController {

	private final CreateCourse createCourse;

	private final FetchCourse fetchCourse;

	private final FetchCourseById fetchCourseById;

	private final UpdateCourse updateCourse;

	private final DeleteCourse deleteCourse;

	private final AssignTeacherToCourse assignTeacherToCourse;

	private final UnassignTeacherFromCourse unassignTeacherFromCourse;

	private final GetAllCourseAssignments getAllCourseAssignments;

	private final GetAssignedTeachers getAssignedTeachers;

	private final CreateScheduledSlot createScheduledSlot;

	private final GetScheduledSlots getScheduledSlots;

	private final DeleteScheduledSlot deleteScheduledSlot;

	/**
	 * Creates a new educational course in the system.
	 * @param createCourseResource the payload containing course details
	 * @return a {@link ResponseEntity} containing the created {@link CourseResource}
	 */
	@Operation(summary = "Create a new course",
			description = "Creates a new course in the system. Restricted to Admin role.")
	@ApiResponse(responseCode = "201", description = "Course successfully created")
	@ApiResponse(responseCode = "400", description = "Invalid input data")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@PostMapping
	public ResponseEntity<Map<String, String>> createCourse(
			@Valid @RequestBody CreateCourseResource createCourseResource) throws CourseAlreadyExistsException {

		CourseDTO courseDTO = CourseResourceMapper.MAPPER.toCourseDTO(createCourseResource);

		CourseDTO createdCourse = createCourse.execute(courseDTO);

		String message = String.format(
				"The course with title '%s' and ID '%d' was successfully registered in the system.",
				createdCourse.getTitle(), createdCourse.getId());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
	}

	/**
	 * Retrieves a list of courses. Can be optionally filtered by title and active status.
	 * @param title the optional title to filter by
	 * @param active the optional active status to filter by (true/false)
	 * @return a {@link ResponseEntity} containing a list of {@link CourseResource}s
	 */
	@Operation(summary = "Get all courses",
			description = "Retrieves a list of all available courses. Optionally filters by title. Accessible to everyone (Public).")
	@ApiResponse(responseCode = "200", description = "List of courses successfully retrieved")
	@GetMapping
	public ResponseEntity<List<CourseResource>> getAllCourses(@RequestParam(required = false) String title,
			@RequestParam(required = false) Boolean active) {

		List<CourseDTO> fetchedCourses = fetchCourse.execute(title, active);

		List<CourseResource> responseResources = fetchedCourses.stream()
			.map(CourseResourceMapper.MAPPER::toCourseResource)
			.toList();

		return ResponseEntity.ok(responseResources);
	}

	/**
	 * Retrieves a specific course by its unique identifier.
	 * @param id the unique identifier of the course
	 * @return a {@link ResponseEntity} containing the requested {@link CourseResource}
	 * @throws ResourceNotFoundException if the course is not found
	 */
	@Operation(summary = "Get course by ID",
			description = "Retrieves a specific course by its unique ID. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "Course details successfully retrieved")
	@ApiResponse(responseCode = "404", description = "Course not found")
	@GetMapping("/{id}")
	public ResponseEntity<CourseResource> getCourseById(@PathVariable Long id) throws ResourceNotFoundException {

		CourseDTO courseDTO = fetchCourseById.execute(id);

		CourseResource courseResource = CourseResourceMapper.MAPPER.toCourseResource(courseDTO);

		return ResponseEntity.ok(courseResource);
	}

	/**
	 * Updates an existing course.
	 * @param id the unique identifier of the course to update
	 * @param updateCourseResource the updated course data
	 * @return a {@link ResponseEntity} containing the updated {@link CourseResource}
	 * @throws ResourceNotFoundException if the course is not found
	 * @throws CourseAlreadyExistsException if the new title clashes with an existing
	 * course
	 */
	@Operation(summary = "Update an existing course",
			description = "Updates a course's details and active status. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "Course successfully updated")
	@ApiResponse(responseCode = "404", description = "Course not found")
	@ApiResponse(responseCode = "409", description = "Course title already exists")
	@PutMapping("/{id}")
	public ResponseEntity<CourseResource> updateCourse(@PathVariable Long id,
			@Valid @RequestBody UpdateCourseResource updateCourseResource)
			throws ResourceNotFoundException, CourseAlreadyExistsException {

		CourseDTO updateData = CourseResourceMapper.MAPPER.toCourseDTO(updateCourseResource);

		CourseDTO updatedCourse = updateCourse.execute(id, updateData);

		CourseResource responseResource = CourseResourceMapper.MAPPER.toCourseResource(updatedCourse);

		return ResponseEntity.ok(responseResource);
	}

	/**
	 * Permanently deletes a specific course from the system.
	 * @param id the unique identifier of the course to delete
	 * @return a {@link ResponseEntity} with no content (HTTP 204)
	 * @throws ResourceNotFoundException if the course is not found
	 */
	@Operation(summary = "Delete a course",
			description = "Permanently deletes a course by its ID. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "Course successfully deleted")
	@ApiResponse(responseCode = "404", description = "Course not found")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable Long id) throws ResourceNotFoundException {

		deleteCourse.execute(id);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Assigns an existing teacher to an existing course.
	 * @param courseId the ID of the course
	 * @param teacherId the ID of the teacher
	 * @return a {@link ResponseEntity} with no content (HTTP 204)
	 * @throws ResourceNotFoundException if the course or teacher is not found
	 */
	@Operation(summary = "Assign a teacher to a course",
			description = "Links a specific teacher to a specific course. Restricted to Admin role.")
	@ApiResponse(responseCode = "204", description = "Teacher successfully assigned to the course")
	@ApiResponse(responseCode = "404", description = "Course or Teacher not found")
	@PostMapping("/{courseId}/teachers/{teacherId}")
	public ResponseEntity<Void> assignTeacherToCourse(@PathVariable Long courseId, @PathVariable Long teacherId)
			throws ResourceNotFoundException {

		assignTeacherToCourse.execute(courseId, teacherId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Removes an assigned teacher from a specific course.
	 * @param courseId the ID of the course
	 * @param teacherId the ID of the teacher
	 * @return a {@link ResponseEntity} with no content (HTTP 204)
	 * @throws ResourceNotFoundException if the course or teacher is not found
	 * @throws IllegalOperationException if the teacher has active scheduled slots
	 */
	@Operation(summary = "Unassign a teacher from a course",
			description = "Removes the link between a specific teacher and a course. Restricted to Admin role."
					+ " Fails if the teacher has scheduled slots for this course.")
	@ApiResponse(responseCode = "204", description = "Teacher successfully unassigned from the course")
	@ApiResponse(responseCode = "403",
			description = "Forbidden: Cannot remove teacher (Illegal Operation due to existing scheduled slots)")
	@ApiResponse(responseCode = "404", description = "Course or Teacher not found")
	@DeleteMapping("/{courseId}/teachers/{teacherId}")
	public ResponseEntity<Void> unassignTeacherFromCourse(@PathVariable Long courseId, @PathVariable Long teacherId)
			throws ResourceNotFoundException, IllegalOperationException {

		unassignTeacherFromCourse.execute(courseId, teacherId);

		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves a list of all course-to-teacher assignments.
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link CourseAssignmentResource}
	 */
	@Operation(summary = "Get all course assignments",
			description = "Retrieves a list of all teachers assigned to all courses. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved assignments list")
	@GetMapping("/assignments")
	public ResponseEntity<List<CourseAssignmentResource>> getAllAssignments() {

		List<CourseAssignmentDTO> assignmentDTOs = getAllCourseAssignments.execute();

		List<CourseAssignmentResource> resources = CourseResourceMapper.MAPPER.toAssignmentResourceList(assignmentDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Retrieves all teachers assigned to a specific course.
	 * @param courseId the ID of the course
	 * @return a {@link ResponseEntity} containing a list of
	 * {@link AssignedTeacherResource}
	 * @throws ResourceNotFoundException if the course is not found
	 */
	@Operation(summary = "Get course teachers",
			description = "Retrieves a list of all teachers assigned to teach a specific course.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved assigned teachers")
	@ApiResponse(responseCode = "404", description = "Course not found")
	@GetMapping("/{courseId}/teachers")
	public ResponseEntity<List<AssignedTeacherResource>> getTeachersForCourse(@PathVariable Long courseId)
			throws ResourceNotFoundException {

		List<AssignedTeacherDTO> teacherDTOs = getAssignedTeachers.execute(courseId);

		List<AssignedTeacherResource> resources = CourseResourceMapper.MAPPER
			.toAssignedTeacherResourceList(teacherDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Creates a new scheduled slot for a specific course. Prevents double-booking of
	 * teachers and classrooms by checking for overlapping time frames.
	 * @param courseId the ID of the course
	 * @param createScheduledSlotResource the payload containing slot details
	 * @return a map with a success message
	 * @throws ResourceNotFoundException if the course or teacher is not found in the
	 * system
	 * @throws TeacherNotAssignedToCourseException if the specified teacher is not
	 * assigned to the given course
	 * @throws IllegalOperationException if there is a time overlap conflict for either
	 * the teacher or the classroom
	 */
	@Operation(summary = "Create a scheduled slot",
			description = "Adds a new timetable slot for a specific course. Restricted to Admin role.")
	@ApiResponse(responseCode = "201", description = "Scheduled slot successfully created")
	@PostMapping("/{courseId}/slots")
	public ResponseEntity<Map<String, String>> createSlot(@PathVariable Long courseId,
			@Valid @RequestBody CreateScheduledSlotResource createScheduledSlotResource)
			throws ResourceNotFoundException, TeacherNotAssignedToCourseException, IllegalOperationException {

		ScheduledSlotDTO scheduledSlotDTO = ScheduledSlotResourceMapper.MAPPER.toSlotDTO(createScheduledSlotResource);

		ScheduledSlotDTO createdSlot = createScheduledSlot.execute(courseId, scheduledSlotDTO);

		String message = String.format(
				"The scheduled slot on '%s' with teacher ID '%d' was successfully created with Slot ID '%d'.",
				createdSlot.getDayOfWeek(), createdSlot.getTeacherId(), createdSlot.getId());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
	}

	/**
	 * Retrieves the scheduled slots for a specific course.
	 * @param courseId the ID of the course
	 * @return a {@link ResponseEntity} containing a list of {@link ScheduledSlotResource}
	 * @throws ResourceNotFoundException if the course is not found
	 */
	@Operation(summary = "Get course scheduled slots",
			description = "Retrieves all timetable slots for a specific course.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of slots")
	@ApiResponse(responseCode = "404", description = "Course not found")
	@GetMapping("/{courseId}/slots")
	public ResponseEntity<List<ScheduledSlotResource>> getCourseSlots(@PathVariable Long courseId)
			throws ResourceNotFoundException {

		List<ScheduledSlotDTO> slotDTOs = getScheduledSlots.execute(courseId);

		List<ScheduledSlotResource> resources = ScheduledSlotResourceMapper.MAPPER.toResourceList(slotDTOs);

		return ResponseEntity.ok(resources);
	}

	/**
	 * Deletes a specific scheduled slot for a given course.
	 * @param courseId the ID of the course
	 * @param slotId the ID of the scheduled slot
	 * @return a map with a success message
	 * @throws ResourceNotFoundException if the course or slot is not found
	 */
	@Operation(summary = "Delete a scheduled slot",
			description = "Removes a timetable slot for a specific course. Restricted to Admin role.")
	@ApiResponse(responseCode = "200", description = "Scheduled slot successfully deleted")
	@ApiResponse(responseCode = "404", description = "Course or Slot not found")
	@DeleteMapping("/{courseId}/slots/{slotId}")
	public ResponseEntity<Map<String, String>> deleteSlot(@PathVariable Long courseId, @PathVariable Long slotId)
			throws ResourceNotFoundException {

		deleteScheduledSlot.execute(courseId, slotId);

		String message = String.format("The scheduled slot with ID '%d' for course ID '%d' was successfully deleted.",
				slotId, courseId);

		return ResponseEntity.ok(Map.of("message", message));
	}

}

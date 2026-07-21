package com.education.tutoring.management.rest.model;

import com.education.tutoring.management.domain.util.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * REST API response detailing a specific student's enrollment status and corresponding
 * course schedule.
 */
@Data
@Schema(description = "Comprehensive details of a student's enrollment in a scheduled class slot")
public class EnrollmentResponseResource {

	@Schema(description = "The unique identifier of the student", example = "1")
	private Long studentId;

	@Schema(description = "The student's first and last name", example = "Christos Lymperis")
	private String studentFullName;

	@Schema(description = "The student's email", example = "c.lymperis@example.com")
	private String studentEmail;

	@Schema(description = "The unique identifier of the enrollment", example = "1")
	private Long enrollmentId;

	@Schema(description = "The date the student was successfully enrolled", example = "2026-05-10")
	private LocalDate enrollmentDate;

	@Schema(description = "The current status of the enrollment (e.g., PENDING_ENROLL, ACTIVE, PENDING_DROP, DROPPED)",
			example = "ACTIVE")
	private EnrollmentStatus status;

	@Schema(description = "The unique identifier of the scheduled slot", example = "1")
	private Long scheduledSlotId;

	@Schema(description = "The title of the enrolled course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The full name of the teacher instructing the course", example = "Dimitris Ioannidis")
	private String teacherName;

	@Schema(description = "The day of the week the course takes place", example = "FRIDAY")
	private DayOfWeek dayOfWeek;

	@Schema(type = "string", description = "The start time of the lesson (Format: HH:mm:ss)", example = "18:00:00")
	private LocalTime startTime;

	@Schema(type = "string", description = "The end time of the lesson (Format: HH:mm:ss)", example = "20:00:00")
	private LocalTime endTime;

	@Schema(description = "The designated physical classroom", example = "SCIENCE LAB")
	private String classroom;

}

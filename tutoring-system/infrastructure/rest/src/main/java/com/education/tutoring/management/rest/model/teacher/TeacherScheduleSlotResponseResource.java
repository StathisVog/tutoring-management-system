package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

/**
 * REST API response representing an individual scheduled class slot within a teacher's
 * personal timetable.
 */
@Data
@Schema(description = "Details of a specific time slot in the teacher's schedule")
public class TeacherScheduleSlotResponseResource {

	@Schema(description = "The unique identifier of the scheduled slot", example = "1")
	private Long slotId;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The ID of the course", example = "1")
	private Long courseId;

	@Schema(description = "The start time of the lesson", example = "16:00:00")
	private LocalTime startTime;

	@Schema(description = "The end time of the lesson", example = "18:00:00")
	private LocalTime endTime;

	@Schema(description = "The classroom where the lesson takes place", example = "SCIENCE LAB", nullable = true)
	private String classroom;

	@Schema(description = "The current status of the lesson (e.g., SCHEDULED, CANCELLED)", example = "CANCELLED")
	private String status;

	@Schema(description = "The reason for cancellation, populated only if an absence is declared",
			example = "Teacher absence", nullable = true)
	private String cancelReason;

	@Schema(description = "The total number of actively enrolled students in this class", example = "12")
	private int enrolledStudentsCount;

}

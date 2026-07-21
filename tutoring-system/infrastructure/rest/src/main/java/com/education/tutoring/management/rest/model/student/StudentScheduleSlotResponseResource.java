package com.education.tutoring.management.rest.model.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

/**
 * REST API response representing an individual scheduled class slot within a student's
 * personal timetable.
 */
@Data
@Schema(description = "Details of a specific time slot in the student's personal schedule")
public class StudentScheduleSlotResponseResource {

	@Schema(description = "The unique identifier of the scheduled slot", example = "1")
	private Long slotId;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The full name of the teacher", example = "Dimitris Ioannidis")
	private String teacherName;

	@Schema(description = "The start time of the lesson", example = "16:00:00")
	private LocalTime startTime;

	@Schema(description = "The end time of the lesson", example = "18:00:00")
	private LocalTime endTime;

	@Schema(description = "The classroom where the lesson takes place. Null implies online.", example = "SCIENCE LAB",
			nullable = true)
	private String classroom;

	@Schema(description = "The current status of the lesson (e.g., SCHEDULED, CANCELLED)", example = "CANCELLED")
	private String status;

	@Schema(description = "The reason for cancellation, populated only if the status is CANCELLED",
			example = "Teacher absence: Medical appointment", nullable = true)
	private String cancelReason;

	@Schema(description = "The unique identifier of the lesson activity assigned to this specific slot and date",
			example = "1", nullable = true)
	private Long activityId;

	@Schema(description = "The syllabus or homework assigned for this lesson",
			example = "Exercises 1-5 from page 40 are due.", nullable = true)
	private String activityDescription;

}

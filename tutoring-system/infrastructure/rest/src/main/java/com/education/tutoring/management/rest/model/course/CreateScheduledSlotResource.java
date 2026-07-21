package com.education.tutoring.management.rest.model.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * REST request payload for scheduling a new class slot within a course's timetable.
 */
@Data
@Schema(description = "Payload for scheduling a new class slot")
public class CreateScheduledSlotResource {

	@NotNull(message = "Teacher ID is required")
	@Schema(description = "The ID of the teacher assigned to the slot", example = "1")
	private Long teacherId;

	@NotNull(message = "Day of week is required")
	@Schema(description = "The day of the week for the slot", example = "MONDAY")
	private DayOfWeek dayOfWeek;

	@NotNull(message = "Start time is required")
	@Schema(type = "string", description = "Format: HH:mm", example = "17:00")
	private LocalTime startTime;

	@NotNull(message = "End time is required")
	@Schema(type = "string", description = "Format: HH:mm", example = "19:00")
	private LocalTime endTime;

	@Schema(description = "The designated room or virtual link for the class", example = "SCIENCE LAB")
	private String classroom;

	@Min(value = 1, message = "Capacity must be at least 1")
	@Schema(description = "The maximum number of students allowed to enroll", example = "10")
	private int capacity;

}

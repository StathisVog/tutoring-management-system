package com.education.tutoring.management.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * REST API response representing a globally scheduled class slot, including course,
 * teacher, and capacity details.
 */
@Data
@Schema(description = "Details of a scheduled class slot available in the system")
public class ScheduledSlotResource {

	@Schema(description = "The unique identifier of the scheduled slot", example = "1")
	private Long id;

	@Schema(description = "The ID of the course", example = "1")
	private Long courseId;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The ID of the teacher assigned to the slot", example = "1")
	private Long teacherId;

	@Schema(description = "The full name of the teacher", example = "Dimitris Ioannidis")
	private String teacherName;

	@Schema(description = "The day of the week the class takes place", example = "MONDAY")
	private DayOfWeek dayOfWeek;

	@Schema(type = "string", example = "17:00", description = "Format: HH:mm")
	private LocalTime startTime;

	@Schema(type = "string", example = "19:00", description = "Format: HH:mm")
	private LocalTime endTime;

	@Schema(description = "The physical or virtual classroom location", example = "SCIENCE LAB")
	private String classroom;

	@Schema(description = "The maximum student capacity for this class", example = "10")
	private int capacity;

	@Schema(description = "The number of currently available seats in this scheduled slot", example = "3")
	private int availableSeats;

}

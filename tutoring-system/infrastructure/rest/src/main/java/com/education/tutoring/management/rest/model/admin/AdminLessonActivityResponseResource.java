package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API resource representation of a lesson activity for administrative monitoring.
 * Used as the data transfer object at the Web layer to serialize comprehensive lesson
 * tracking data (including course, teacher, and syllabus details) into JSON API
 * responses.
 */
@Data
@Schema(description = "Comprehensive details of a lesson activity for administrative monitoring")
public class AdminLessonActivityResponseResource {

	@Schema(description = "The unique identifier of the class slot", example = "1")
	private Long slotId;

	@Schema(description = "The unique identifier of the course subject", example = "1")
	private Long courseId;

	@Schema(description = "The title of the course", example = "Mathematics")
	private String courseTitle;

	@Schema(description = "The unique identifier of the assigned teacher", example = "1")
	private Long teacherId;

	@Schema(description = "The full name of the instructor", example = "George Papadopoulos")
	private String teacherFullName;

	@Schema(description = "The unique identifier of the lesson activity", example = "1")
	private Long activityId;

	@Schema(description = "The date of the lesson activity", example = "2026-05-26")
	private LocalDate date;

	@Schema(description = "The syllabus, homework or topics covered", example = "Exercises 1-5.")
	private String description;

}

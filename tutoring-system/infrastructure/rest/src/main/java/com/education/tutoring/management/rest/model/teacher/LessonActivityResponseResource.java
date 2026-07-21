package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API response detailing a completed lesson log or a planned syllabus activity from
 * the teacher's perspective.
 */
@Data
@Schema(description = "Details of a recorded or planned lesson activity")
public class LessonActivityResponseResource {

	@Schema(description = "The unique identifier of the lesson activity log", example = "1")
	private Long id;

	@Schema(description = "The unique identifier of the scheduled slot", example = "1")
	private Long slotId;

	@Schema(description = "The title of the course associated with this activity",
			example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The date the lesson took place or is scheduled to happen", example = "2026-05-22")
	private LocalDate date;

	@Schema(description = "The topics covered or planned syllabus",
			example = "Study of organic chemistry, chemical kinetics, and ionic equilibrium. Focus on methodology and speed for the national exams.")
	private String description;

}

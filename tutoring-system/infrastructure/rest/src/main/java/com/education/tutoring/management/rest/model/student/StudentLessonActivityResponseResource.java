package com.education.tutoring.management.rest.model.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API response detailing a specific lesson's activity, syllabus, or homework from
 * the student's perspective.
 */
@Data
@Schema(description = "Details of a lesson activity tailored for student view, including course and teacher details")
public class StudentLessonActivityResponseResource {

	@Schema(description = "The unique identifier of the lesson activity", example = "1")
	private Long activityId;

	@Schema(description = "The unique identifier of the class slot", example = "1")
	private Long slotId;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The full name of the instructor who teaches this class", example = "Dimitris Ioannidis")
	private String teacherFullName;

	@Schema(description = "The date of the lesson activity", example = "2026-05-26")
	private LocalDate date;

	@Schema(description = "The syllabus, homework or topics covered",
			example = "Exercises 1-5 from page 40 are due next lesson.")
	private String description;

}

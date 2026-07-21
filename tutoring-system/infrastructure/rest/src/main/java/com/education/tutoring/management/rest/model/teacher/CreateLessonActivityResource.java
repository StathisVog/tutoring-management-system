package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST request payload used by teachers to log completed lesson activities or plan future
 * syllabus items.
 */
@Data
@Schema(description = "Payload for recording a lesson log (past) or planning a syllabus activity (future)")
public class CreateLessonActivityResource {

	@NotNull(message = "Lesson date is required")
	@Schema(description = "The date the lesson took place", example = "2026-05-22")
	private LocalDate date;

	@NotBlank(message = "Lesson description/topics covered is required")
	@Schema(description = "Detailed log of the topics covered or planned syllabus/homework",
			example = "Classwork: Introduction to Chemical Equilibrium. Reversible reactions and the Equilibrium Constant.\n"
					+ " Homework: Textbook Chapter 4, Exercises 1-4 on page 112.")
	private String description;

}

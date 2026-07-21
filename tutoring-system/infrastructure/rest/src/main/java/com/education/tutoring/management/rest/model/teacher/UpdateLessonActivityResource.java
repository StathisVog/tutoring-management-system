package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * REST request payload used by teachers to modify the description of an existing lesson
 * activity log.
 */
@Data
@Schema(description = "Payload for updating an existing lesson activity log or syllabus plan")
public class UpdateLessonActivityResource {

	@NotBlank(message = "Description is required")
	@Schema(description = "The updated content, topics covered, or assigned homework",
			example = "Updated topics: Classwork: Introduction to Chemical Equilibrium. Reversible reactions and the Equilibrium Constant.\n"
					+ " Homework: Textbook Chapter 4, Exercises 5-8 on page 114.")
	private String description;

}

package com.education.tutoring.management.rest.model.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * REST request payload containing the necessary information to create a new course.
 */
@Data
@Schema(description = "Payload for creating a new educational course")
public class CreateCourseResource {

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	@NotBlank(message = "The course title is required.")
	@Size(min = 3, max = 100, message = "The title must be between 3 and 100 characters.")
	private String title;

	@Schema(description = "Detailed description of the course contents",
			example = "Study of organic chemistry, chemical kinetics, and ionic equilibrium. Focus on methodology and speed for the national exams.")
	@Size(max = 500, message = "The description cannot exceed 500 characters.")
	private String description;

	@Schema(description = "The target grade or class level", example = "C_LUKEIOU")
	@NotBlank(message = "The grade level is required.")
	@Size(max = 40, message = "The grade level cannot exceed 40 characters.")
	private String gradeLevel;

}

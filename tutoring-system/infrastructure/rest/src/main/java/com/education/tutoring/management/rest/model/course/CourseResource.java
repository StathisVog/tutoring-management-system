package com.education.tutoring.management.rest.model.course;

import com.education.tutoring.management.rest.model.user.TeacherResource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * REST API response representing a comprehensive educational course and its assigned
 * teachers.
 */
@Data
@Schema(description = "Comprehensive details of an educational course")
public class CourseResource {

	@Schema(description = "The unique identifier of the course", example = "1")
	private Long id;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String title;

	@Schema(description = "Detailed description of the course contents",
			example = "Study of organic chemistry, chemical kinetics, and ionic equilibrium. Focus on methodology and speed for the national exams.")
	private String description;

	@Schema(description = "The target grade or class level", example = "C_LUKEIOU")
	private String gradeLevel;

	@Schema(description = "Indicates whether the course is currently active", example = "true")
	private boolean active;

	@Schema(description = "List of teachers currently assigned to the course")
	private List<TeacherResource> teachers;

}

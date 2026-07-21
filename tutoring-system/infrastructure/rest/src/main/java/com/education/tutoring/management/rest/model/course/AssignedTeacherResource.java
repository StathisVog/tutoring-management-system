package com.education.tutoring.management.rest.model.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * REST API response representing a teacher currently assigned to a specific course.
 */
@Data
@Schema(description = "Details of a teacher assigned to a course")
public class AssignedTeacherResource {

	@Schema(description = "The unique identifier of the teacher", example = "1")
	private Long id;

	@Schema(description = "The full name of the teacher", example = "Dimitris Ioannidis")
	private String fullName;

	@Schema(description = "The email address of the teacher", example = "dioannidis@example.com")
	private String email;

	@Schema(description = "The specialty of the teacher", example = "Physicist / Chemist")
	private String specialty;

}

package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * REST API response representing an enrolled, active student within a specific class
 * roster.
 */
@Data
@Schema(description = "Represents an active student enrolled in a specific class")
public class ActiveStudentResource {

	@Schema(description = "The unique identifier of the student", example = "1")
	private Long id;

	@Schema(description = "The full name of the student", example = "Christos Lymperis")
	private String fullName;

	@Schema(description = "The system username of the student", example = "clymperis")
	private String username;

}

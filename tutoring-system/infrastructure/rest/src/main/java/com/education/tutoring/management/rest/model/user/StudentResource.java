package com.education.tutoring.management.rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * REST API response representing a student's profile, extending the base user resource
 * with academic details.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Detailed profile information specific to a student user")
public class StudentResource extends UserResource {

	@Schema(description = "The age of the student", example = "18")
	private Integer age;

	@Schema(description = "The student's current school class or grade level", example = "C_LUKEIOU")
	private String schoolClass;

	@Schema(description = "The full name of the student's parent", example = "Katerina Lymperi")
	private String parentFullName;

	@Schema(description = "The tax identification number (AFM) of the parent", example = "223344556")
	private String parentTaxId;

}

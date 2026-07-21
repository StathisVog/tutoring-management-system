package com.education.tutoring.management.rest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * REST request payload for registering a new student account.
 */
@Data
@Schema(description = "Payload containing necessary information to register a new student")
public class RegisterStudentResource {

	@Schema(description = "The desired username for the student", example = "clymperis")
	private String username;

	@Schema(description = "The student's contact email address", example = "c.lymperis@example.com")
	private String email;

	@Schema(description = "The student's raw plaintext password")
	private String password;

	@Schema(description = "The full name of the student", example = "Christos Lymperis")
	private String fullName;

	@Schema(description = "The home address of the student", example = "Athens, Greece")
	private String address;

	@Schema(description = "The age of the student", example = "18")
	private int age;

	@Schema(description = "The student's current school class or grade level", example = "C_LUKEIOU")
	private String schoolClass;

	@Schema(description = "The full name of the student's parent", example = "Katerina Lymperi")
	private String parentFullName;

	@Schema(description = "The tax identification number (AFM) of the parent", example = "223344556")
	private String parentTaxId;

}

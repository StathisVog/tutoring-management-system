package com.education.tutoring.management.rest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * REST request payload for registering a new teacher account along with their eligible
 * courses.
 */
@Data
@Schema(description = "Payload containing necessary information to register a new teacher")
public class RegisterTeacherResource {

	@Schema(description = "The desired username for the teacher", example = "dioannidis")
	private String username;

	@Schema(description = "The teacher's contact email address", example = "dioannidis@example.com")
	private String email;

	@Schema(description = "The teacher's raw plaintext password")
	private String password;

	@Schema(description = "The full name of the teacher", example = "Dimitris Ioannidis")
	private String fullName;

	@Schema(description = "The home address of the teacher", example = "Athens, Greece")
	private String address;

	@Schema(description = "The primary subject specialty of the teacher", example = "Physicist / Chemist")
	private String specialty;

	@Schema(description = "A short biography or academic background of the teacher",
			example = "PhD in Theoretical Physics.")
	private String bio;

	@Schema(description = "A list of course IDs the teacher is eligible to teach", example = "[1, 2, 5]")
	private List<Long> eligibleCourseIds;

}

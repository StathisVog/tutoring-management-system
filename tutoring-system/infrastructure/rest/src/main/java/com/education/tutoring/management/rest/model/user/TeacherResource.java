package com.education.tutoring.management.rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * REST API response representing a teacher's profile, extending the base user resource
 * with professional details.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Detailed profile information specific to a teacher user")
public class TeacherResource extends UserResource {

	@Schema(description = "The primary subject specialty of the teacher", example = "Physicist / Chemist")
	private String specialty;

	@Schema(description = "A short biography or academic background", example = "PhD in Theoretical Physics.")
	private String bio;

	// the courses to which the Admin has officially assigned him
	@Schema(description = "The list of course titles to which the Admin has officially assigned this teacher",
			example = "[\"Physics\"]")
	private List<String> courses;

	// the courses chosen by the teacher during registration
	@Schema(description = "The list of course titles chosen by the teacher during registration as eligible to teach",
			example = "[\"Physics\", \"Chemistry\"]")
	private List<String> eligibleCourses;

}

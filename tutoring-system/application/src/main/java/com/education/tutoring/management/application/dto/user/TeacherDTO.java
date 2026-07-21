package com.education.tutoring.management.application.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Data Transfer Object representing a teacher, extending the base UserDTO.
 */
@Data
// callSuper = true => It also includes the fields of the Parent Class (UserDTO)
@EqualsAndHashCode(callSuper = true)
public class TeacherDTO extends UserDTO {

	private String specialty;

	private String bio;

	// the courses to which the Admin has officially assigned him
	private List<String> courses;

	// the courses chosen by the teacher during registration
	private List<String> eligibleCourses;

}

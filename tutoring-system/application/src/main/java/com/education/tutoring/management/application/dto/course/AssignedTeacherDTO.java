package com.education.tutoring.management.application.dto.course;

import lombok.Data;

/**
 * Data Transfer Object representing a teacher assigned to a specific course.
 */
@Data
public class AssignedTeacherDTO {

	private Long id;

	private String fullName;

	private String email;

	private String specialty;

}

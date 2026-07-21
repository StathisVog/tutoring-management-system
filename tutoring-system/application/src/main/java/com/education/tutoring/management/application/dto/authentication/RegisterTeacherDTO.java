package com.education.tutoring.management.application.dto.authentication;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for teacher registration requests.
 */
@Data
public class RegisterTeacherDTO {

	private String username;

	private String email;

	private String password;

	private String fullName;

	private String address;

	private String specialty;

	private String bio;

	private List<Long> eligibleCourseIds;

}

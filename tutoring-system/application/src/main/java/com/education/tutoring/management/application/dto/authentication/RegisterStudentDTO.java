package com.education.tutoring.management.application.dto.authentication;

import lombok.Data;

/**
 * Data Transfer Object for student registration requests.
 */
@Data
public class RegisterStudentDTO {

	private String username;

	private String email;

	private String password;

	private String fullName;

	private String address;

	private int age;

	private String schoolClass;

	private String parentFullName;

	private String parentTaxId;

}

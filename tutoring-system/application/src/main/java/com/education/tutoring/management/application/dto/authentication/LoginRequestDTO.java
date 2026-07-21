package com.education.tutoring.management.application.dto.authentication;

import lombok.Data;

/**
 * Data Transfer Object for login requests.
 */
@Data
public class LoginRequestDTO {

	private String username;

	private String password;

}

package com.education.tutoring.management.application.dto.user;

import lombok.Data;

/**
 * Data Transfer Object for password update requests.
 */
@Data
public class UpdatePasswordDTO {

	private String oldPassword;

	private String newPassword;

	private String confirmNewPassword;

}

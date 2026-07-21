package com.education.tutoring.management.rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * REST request payload used by authenticated users to securely update their account
 * password.
 */
@Data
@Schema(description = "Payload containing necessary fields to update a user's password")
public class UpdatePasswordResource {

	@NotBlank(message = "Old password is required.")
	@Schema(description = "The user's current password")
	private String oldPassword;

	@NotBlank(message = "New password is required.")
	@Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters.")
	@Schema(description = "The desired new password")
	private String newPassword;

	@NotBlank(message = "Password confirmation is required.")
	@Schema(description = "Confirmation of the new password to prevent typos")
	private String confirmNewPassword;

}

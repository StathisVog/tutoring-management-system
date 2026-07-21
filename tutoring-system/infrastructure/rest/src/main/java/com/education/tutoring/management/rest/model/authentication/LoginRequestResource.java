package com.education.tutoring.management.rest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * REST request payload containing user credentials for authentication.
 */
@Data
@Schema(description = "Payload for user login authentication")
public class LoginRequestResource {

	@Schema(description = "The user's registered username", example = "clymperis")
	private String username;

	@Schema(description = "The user's plaintext password")
	private String password;

}

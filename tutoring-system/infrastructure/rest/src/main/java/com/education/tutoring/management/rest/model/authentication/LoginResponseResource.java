package com.education.tutoring.management.rest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST API response returned upon successful authentication. Delivers the user's role to
 * the frontend for UI routing, while the JWT is handled via HttpOnly cookie.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response payload returned upon successful authentication containing the user's role")
public class LoginResponseResource {

	@Schema(description = "The assigned authority role of the authenticated user", example = "TEACHER")
	private String role;

}

package com.education.tutoring.management.application.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the result of a successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {

	private String token;

	private String role;

}

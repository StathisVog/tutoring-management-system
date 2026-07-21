package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.user.UserDTO;

/**
 * Interface for generating JSON Web Tokens (JWT) for authenticated users.
 */
public interface JwtTokenGenerator {

	/**
	 * Generates a JWT token for the specified user based on their details and role.
	 * @param userDTO the data transfer object containing the user's profile information
	 * @return a string representing the generated JWT token
	 */
	String generateToken(UserDTO userDTO);

}

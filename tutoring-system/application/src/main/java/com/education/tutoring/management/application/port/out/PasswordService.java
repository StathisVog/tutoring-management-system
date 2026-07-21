package com.education.tutoring.management.application.port.out;

/**
 * Interface for password encoding operations.
 */
public interface PasswordService {

	/**
	 * Securely hashes a raw plaintext password.
	 * @param rawPassword the plaintext password to be hashed
	 * @return the resulting cryptographic hash
	 */
	String encode(String rawPassword);

	/**
	 * Verifies whether a raw plaintext password matches a previously encoded password
	 * hash.
	 * @param rawPassword the plaintext password provided by the user
	 * @param encodedPassword the stored cryptographic hash to compare against
	 * @return {@code true} if the raw password matches the encoded hash, {@code false}
	 * otherwise
	 */
	boolean matches(String rawPassword, String encodedPassword);

}

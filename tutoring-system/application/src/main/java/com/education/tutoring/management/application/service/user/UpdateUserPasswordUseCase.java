package com.education.tutoring.management.application.service.user;

import com.education.tutoring.management.application.dto.user.UpdatePasswordDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.user.UpdateUserPassword;
import com.education.tutoring.management.application.port.out.PasswordService;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UpdateUserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for securely updating an authenticated user's password.
 */
@Slf4j
@UseCase
class UpdateUserPasswordUseCase implements UpdateUserPassword {

	private final UserPersistence userPersistence;

	private final PasswordService passwordService;

	public UpdateUserPasswordUseCase(UserPersistence userPersistence, PasswordService passwordService) {
		this.userPersistence = userPersistence;
		this.passwordService = passwordService;
	}

	/**
	 * Executes the password update process after verifying business and security rules.
	 * @param username the username of the user requesting the change
	 * @param command the DTO containing old, new, and confirmation passwords
	 * @throws ResourceNotFoundException if the user does not exist
	 * @throws UpdateUserException if any password business rule is violated (e.g.,
	 * incorrect old password, mismatch)
	 */
	@Override
	public void execute(String username, UpdatePasswordDTO command)
			throws ResourceNotFoundException, UpdateUserException {

		log.info("User '{}' initiated a password update request.", username);

		UserDTO user = userPersistence.findByUsername(username).orElseThrow(() -> {
			log.warn("Password update failed: User '{}' not found in the system.", username);
			return new ResourceNotFoundException("User not found in the system.");
		});

		// Is the old password correct?
		if (!passwordService.matches(command.getOldPassword(), user.getPasswordHash())) {
			log.warn("Password update failed for '{}': Incorrect current password provided.", username);
			throw new UpdateUserException("The current password you entered is incorrect.");
		}

		// Do the two new passwords match?
		if (!command.getNewPassword().equals(command.getConfirmNewPassword())) {
			log.warn("Password update failed for '{}': New password and confirmation do not match.", username);
			throw new UpdateUserException("The new passwords do not match. Please try again.");
		}

		// Check if user enters the same password
		if (passwordService.matches(command.getNewPassword(), user.getPasswordHash())) {
			log.warn("Password update failed for '{}': New password is the same as the current password.", username);
			throw new UpdateUserException("Your new password must be different from your current password.");
		}

		// Hash the new password
		String newPasswordHash = passwordService.encode(command.getNewPassword());

		userPersistence.updatePasswordHash(username, newPasswordHash);

		log.info("User '{}' successfully updated their password.", username);
	}

}

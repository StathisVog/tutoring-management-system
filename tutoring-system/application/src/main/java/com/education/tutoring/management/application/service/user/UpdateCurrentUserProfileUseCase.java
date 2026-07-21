package com.education.tutoring.management.application.service.user;

import com.education.tutoring.management.application.dto.user.TeacherDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.user.UpdateCurrentUserProfile;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UpdateUserException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Use case implementation for allowing an authenticated user to update their own profile
 * details.
 */
@Slf4j
@UseCase
class UpdateCurrentUserProfileUseCase implements UpdateCurrentUserProfile {

	private final UserPersistence userPersistence;

	public UpdateCurrentUserProfileUseCase(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Executes the profile update process for the current user.
	 * @param username the username of the user requesting the update
	 * @param updatedData the incoming DTO with the new profile data
	 * @return the updated {@link UserDTO} reflecting the changes
	 * @throws ResourceNotFoundException if the user cannot be found
	 * @throws UpdateUserException if the new data violates domain rules or validation
	 * constraints
	 */
	@Override
	public UserDTO execute(String username, UserDTO updatedData) throws ResourceNotFoundException, UpdateUserException {

		log.info("User '{}' is attempting to update their profile.", username);

		validateUpdatePayload(username, updatedData);

		UserDTO savedUser = userPersistence.updateProfile(username, updatedData);
		log.info("User '{}' successfully updated their profile.", username);

		return savedUser;
	}

	/**
	 * Strict validation for updating profile information.
	 * @param currentUsername the username of the user attempting the update
	 * @param updatedData the incoming data to validate
	 * @throws UpdateUserException if any validation rule is breached
	 */
	private void validateUpdatePayload(String currentUsername, UserDTO updatedData) throws UpdateUserException {

		// Full Name Validation
		if (updatedData.getFullName() == null || updatedData.getFullName().trim().isEmpty()) {
			log.warn("Profile update failed for '{}': Full name is empty.", currentUsername);
			throw new UpdateUserException("Full name cannot be empty.");
		}

		// Strict Email Regex Validation
		String strictEmailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
		if (updatedData.getEmail() == null || !updatedData.getEmail().matches(strictEmailRegex)) {
			log.warn("Profile update failed for '{}': Invalid email format provided.", currentUsername);
			throw new UpdateUserException("Invalid email format. Please provide a valid email address.");
		}

		// Email Uniqueness
		Optional<UserDTO> existingEmailUser = userPersistence.findByEmail(updatedData.getEmail());
		if (existingEmailUser.isPresent() && !existingEmailUser.get().getUsername().equals(currentUsername)) {
			log.warn("Profile update failed for '{}': Email '{}' is already in use by another account.",
					currentUsername, updatedData.getEmail());
			throw new UpdateUserException("This email is already in use by another account.");
		}

		// Role-Specific Validation (Teacher)
		if (updatedData instanceof TeacherDTO teacherDTO) {
			if (teacherDTO.getSpecialty() == null || teacherDTO.getSpecialty().trim().isEmpty()) {
				log.warn("Profile update failed for teacher '{}': Specialty is missing.", currentUsername);
				throw new UpdateUserException("Specialty is required for teachers.");
			}
		}
	}

}

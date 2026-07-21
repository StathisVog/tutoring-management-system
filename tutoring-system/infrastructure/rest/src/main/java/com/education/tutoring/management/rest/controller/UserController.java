package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.user.UpdatePasswordDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.in.user.GetCurrentUser;
import com.education.tutoring.management.application.port.in.user.UpdateCurrentUserProfile;
import com.education.tutoring.management.application.port.in.user.UpdateUserPassword;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.exception.UpdateUserException;
import com.education.tutoring.management.rest.adapter.UserResourceMapper;
import com.education.tutoring.management.rest.model.user.UpdatePasswordResource;
import com.education.tutoring.management.rest.model.user.UserResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller acting as the primary driving adapter for unified user profile
 * management. Exposes common endpoints available to all authenticated entities across the
 * system (Students, Teachers, and Admins). Orchestrates core use cases for retrieving the
 * currently authenticated session details, modifying personal profile information, and
 * executing secure password updates.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations concerning all registered users (Students, Teachers, and Admins)")
public class UserController {

	private final GetCurrentUser getCurrentUser;

	private final UpdateCurrentUserProfile updateCurrentUserProfile;

	private final UpdateUserPassword updateUserPassword;

	/**
	 * Retrieves the profile details of the currently authenticated user.
	 * @param principal the security principal containing the authenticated user's
	 * username
	 * @return the safe public profile data of the user
	 * @throws ResourceNotFoundException if the user record is not found in the system
	 */
	@Operation(summary = "Get current user profile",
			description = "Returns the details of the currently authenticated user based on the provided JWT token.")
	@ApiResponse(responseCode = "200", description = "User profile successfully retrieved")
	@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping("/me")
	public ResponseEntity<UserResource> getCurrentUser(Principal principal) throws ResourceNotFoundException {

		UserDTO currentUserDTO = getCurrentUser.execute(principal.getName());

		UserResource resource = UserResourceMapper.MAPPER.toResource(currentUserDTO);

		return ResponseEntity.ok(resource);
	}

	/**
	 * Updates the profile details of the currently authenticated user.
	 * @param principal the security principal containing the username
	 * @param updatedResource the polymorphic resource containing updated data
	 * @return the updated user profile
	 * @throws ResourceNotFoundException if the authenticated user's record is missing
	 * from the database
	 * @throws UpdateUserException if the provided profile data fails business validation
	 * rules (e.g., invalid email format or email already in use)
	 */
	@Operation(summary = "Update current user profile",
			description = "Updates the allowed profile fields for the authenticated user.")
	@ApiResponse(responseCode = "200", description = "Profile successfully updated")
	@PutMapping("/me")
	public ResponseEntity<UserResource> updateCurrentUserProfile(Principal principal,
			@RequestBody UserResource updatedResource) throws ResourceNotFoundException, UpdateUserException {

		UserDTO updateDTO = UserResourceMapper.MAPPER.toDTO(updatedResource);

		UserDTO savedDTO = updateCurrentUserProfile.execute(principal.getName(), updateDTO);

		UserResource resource = UserResourceMapper.MAPPER.toResource(savedDTO);
		return ResponseEntity.ok(resource);
	}

	/**
	 * Updates the password of the currently authenticated user.
	 * @param principal the security principal containing the username
	 * @param resource the request body containing old and new passwords
	 * @return an empty 200 OK response if successful
	 * @throws ResourceNotFoundException if the authenticated user's record is missing
	 * @throws UpdateUserException if password business rules are violated
	 */
	@Operation(summary = "Update current user password",
			description = "Verifies the old password and securely updates it to a new one.")
	@ApiResponse(responseCode = "200", description = "Password successfully updated")
	@PutMapping("/me/password")
	public ResponseEntity<Void> updateCurrentUserPassword(Principal principal,
			@Valid @RequestBody UpdatePasswordResource resource) throws ResourceNotFoundException, UpdateUserException {

		UpdatePasswordDTO command = UserResourceMapper.MAPPER.toUpdatePasswordDTO(resource);

		updateUserPassword.execute(principal.getName(), command);

		return ResponseEntity.ok().build();
	}

}

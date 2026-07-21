package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.application.dto.user.TeacherDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import com.education.tutoring.management.domain.util.Role;
import com.education.tutoring.management.repository.jpa.entity.Student;
import com.education.tutoring.management.repository.jpa.entity.Teacher;
import com.education.tutoring.management.repository.jpa.entity.User;
import com.education.tutoring.management.repository.jpa.mapper.EntityMapper;
import com.education.tutoring.management.repository.jpa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of the {@link UserPersistence} output port. Acts as the
 * foundational persistence adapter managing core user operations across the system.
 * Handles credential validations (such as checking username and email uniqueness) and
 * provides polymorphic data retrieval, seamlessly mapping the base User entity to its
 * specific subtype representations (e.g., Student or Teacher) for authentication and
 * profile management.
 */
@Service
@AllArgsConstructor
public class UserPersistenceImpl implements UserPersistence {

	private final UserRepository userRepository;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Checks if the specified username exists in the system.
	 * @param username the username to check
	 * @return true if the username exists, false otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	/**
	 * Checks if the specified email exists in the system.
	 * @param email the email to check
	 * @return true if the email exists, false otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	/**
	 * Retrieves a user by their username, returning the appropriate polymorphic DTO.
	 * @param username the username to search for
	 * @return an {@link Optional} containing the user DTO if found, or empty otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserDTO> findByUsername(String username) {
		return userRepository.findByUsername(username).map(mapper::toUserDTO);
	}

	/**
	 * Retrieves a user by their unique identifier, returning the appropriate polymorphic
	 * DTO.
	 * @param id the unique identifier of the user
	 * @return an {@link Optional} containing the user DTO if found, or empty otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserDTO> findById(Long id) {
		return userRepository.findById(id).map(mapper::toUserDTO);
	}

	/**
	 * Finds a user by their email and maps the entity to a polymorphic DTO.
	 * @param email the email address to search for
	 * @return an {@link Optional} containing the mapped {@link UserDTO}, or empty
	 * otherwise
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserDTO> findByEmail(String email) {
		return userRepository.findByEmail(email).map(mapper::toUserDTO);
	}

	/**
	 * Activates a user account. Requires an active transaction as it modifies the
	 * database state.
	 * @param id the unique identifier of the user
	 */
	@Override
	@Transactional
	public void activateUser(Long id) {
		userRepository.activateUser(id);
	}

	/**
	 * Retrieves users from the database, mapped to their specific polymorphic DTOs. If no
	 * role is specified, it returns a comprehensive list of all users.
	 * @param role the {@link Role} to filter the users by, or null to bypass filtering
	 * @return a {@link List} of {@link UserDTO} containing user details
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserDTO> findAllUsers(Role role) {

		List<User> users = (role == null) ? userRepository.findAll() : userRepository.findAllByRole(role);

		return users.stream().map(mapper::toUserDTO).toList();
	}

	/**
	 * Deletes a user account from the system by their unique identifier.
	 * @param id the unique identifier of the user to delete
	 */
	@Override
	@Transactional
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	/**
	 * Updates a user's profile by fetching the existing entity and modifying only the
	 * allowed fields.
	 * @param username the username of the user being updated
	 * @param updatedData the DTO containing the new profile values
	 * @return the updated {@link UserDTO} mapped from the saved entity
	 * @throws ResourceNotFoundException if the user is not found in the repository
	 */
	@Override
	@Transactional
	public UserDTO updateProfile(String username, UserDTO updatedData) throws ResourceNotFoundException {

		User existingUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Update only common fields that are allowed to change
		existingUser.setEmail(updatedData.getEmail());
		existingUser.setFullName(updatedData.getFullName());
		existingUser.setAddress(updatedData.getAddress());
		existingUser.setThemeColor(updatedData.getThemeColor());
		existingUser.setAvatarName(updatedData.getAvatarName());

		// Update Teacher specific fields
		if (existingUser instanceof Teacher existingTeacher && updatedData instanceof TeacherDTO incomingTeacher) {
			existingTeacher.setSpecialty(incomingTeacher.getSpecialty());
			existingTeacher.setBio(incomingTeacher.getBio());
		}

		// Update Student specific fields
		if (existingUser instanceof Student existingStudent && updatedData instanceof StudentDTO incomingStudent) {
			existingStudent.setAge(incomingStudent.getAge());
			existingStudent.setSchoolClass(incomingStudent.getSchoolClass());
			existingStudent.setParentFullName(incomingStudent.getParentFullName());
			existingStudent.setParentTaxId(incomingStudent.getParentTaxId());
		}

		return mapper.toUserDTO(userRepository.save(existingUser));
	}

	/**
	 * Updates a user's password hash by fetching the existing entity and modifying the
	 * hash.
	 * @param username the username of the user being updated
	 * @param newPasswordHash the newly encoded password hash
	 * @throws ResourceNotFoundException if the user is not found in the repository
	 */
	@Override
	@Transactional
	public void updatePasswordHash(String username, String newPasswordHash) throws ResourceNotFoundException {

		User existingUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		existingUser.setPasswordHash(newPasswordHash);

		userRepository.save(existingUser);
	}

}

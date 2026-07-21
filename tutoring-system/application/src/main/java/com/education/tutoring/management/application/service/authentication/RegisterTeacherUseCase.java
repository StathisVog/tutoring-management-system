package com.education.tutoring.management.application.service.authentication;

import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.application.port.in.authentication.RegisterTeacher;
import com.education.tutoring.management.application.port.out.PasswordService;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.application.util.RegistrationValidatorUtil;
import com.education.tutoring.management.domain.exception.RegisterUserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for teacher registration. Handles validation, password hashing,
 * and delegation to the persistence layer.
 */
@Slf4j
@UseCase
class RegisterTeacherUseCase implements RegisterTeacher {

	private final TeacherPersistence teacherPersistence;

	private final PasswordService passwordService;

	private final RegistrationValidatorUtil registrationValidatorUtil;

	public RegisterTeacherUseCase(TeacherPersistence teacherPersistence, PasswordService passwordService,
			UserPersistence userPersistence) {
		this.teacherPersistence = teacherPersistence;
		this.passwordService = passwordService;
		this.registrationValidatorUtil = new RegistrationValidatorUtil(userPersistence);
	}

	/**
	 * Executes the registration process for a new teacher.
	 * @param registerTeacherDTO the data transfer object containing the teacher's
	 * registration details
	 * @throws RegisterUserException if validation fails or the user cannot be registered
	 */
	@Override
	public void execute(RegisterTeacherDTO registerTeacherDTO) throws RegisterUserException {

		log.info("Starting registration process for teacher username: {}", registerTeacherDTO.getUsername());

		validateTeacher(registerTeacherDTO);

		String passwordHash = passwordService.encode(registerTeacherDTO.getPassword());

		teacherPersistence.saveTeacherFromRegister(registerTeacherDTO, passwordHash);

		log.info("Successfully registered teacher with username: {}", registerTeacherDTO.getUsername());
	}

	/**
	 * Validates the teacher-specific registration data.
	 * @param registerTeacherDTO the data transfer object to validate
	 * @throws RegisterUserException if any mandatory field is missing or invalid
	 */
	private void validateTeacher(RegisterTeacherDTO registerTeacherDTO) throws RegisterUserException {

		registrationValidatorUtil.validateCredentials(registerTeacherDTO.getUsername(), registerTeacherDTO.getEmail(),
				registerTeacherDTO.getPassword(), registerTeacherDTO.getFullName(), registerTeacherDTO.getAddress());

		if (registerTeacherDTO.getSpecialty() == null || registerTeacherDTO.getSpecialty().isBlank()) {
			log.warn("Teacher registration failed: Specialty is missing for username: {}",
					registerTeacherDTO.getUsername());
			throw new RegisterUserException("Specialty is required");
		}

		if (registerTeacherDTO.getEligibleCourseIds() == null || registerTeacherDTO.getEligibleCourseIds().isEmpty()) {
			log.warn("Teacher registration failed: No eligible courses selected for username: {}",
					registerTeacherDTO.getUsername());
			throw new RegisterUserException("You must select at least one course you are eligible to teach.");
		}
	}

}

package com.education.tutoring.management.application.service.authentication;

import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.port.in.authentication.RegisterStudent;
import com.education.tutoring.management.application.port.out.PasswordService;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.port.out.UserPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.application.util.RegistrationValidatorUtil;
import com.education.tutoring.management.domain.exception.RegisterUserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for student registration. Handles validation, password hashing,
 * and delegation to the persistence layer.
 */
@Slf4j
@UseCase
class RegisterStudentUseCase implements RegisterStudent {

	private final StudentPersistence studentPersistence;

	private final PasswordService passwordService;

	private final RegistrationValidatorUtil registrationValidatorUtil;

	public RegisterStudentUseCase(StudentPersistence studentPersistence, PasswordService passwordService,
			UserPersistence userPersistence) {
		this.studentPersistence = studentPersistence;
		this.passwordService = passwordService;
		this.registrationValidatorUtil = new RegistrationValidatorUtil(userPersistence);
	}

	/**
	 * Executes the registration process for a new student.
	 * @param registerStudentDTO the data transfer object containing the student's
	 * registration details
	 * @throws RegisterUserException if validation fails or the user cannot be registered
	 */
	@Override
	public void execute(RegisterStudentDTO registerStudentDTO) throws RegisterUserException {

		log.info("Starting registration process for student username: {}", registerStudentDTO.getUsername());

		validateStudent(registerStudentDTO);

		String passwordHash = passwordService.encode(registerStudentDTO.getPassword());

		studentPersistence.saveStudentFromRegister(registerStudentDTO, passwordHash);

		log.info("Successfully registered student with username: {}", registerStudentDTO.getUsername());
	}

	/**
	 * Validates the student-specific registration data.
	 * @param registerStudentDTO the data transfer object to validate
	 * @throws RegisterUserException if any mandatory field is missing or invalid
	 */
	private void validateStudent(RegisterStudentDTO registerStudentDTO) throws RegisterUserException {

		registrationValidatorUtil.validateCredentials(registerStudentDTO.getUsername(), registerStudentDTO.getEmail(),
				registerStudentDTO.getPassword(), registerStudentDTO.getFullName(), registerStudentDTO.getAddress());

		if (registerStudentDTO.getAge() < 5 || registerStudentDTO.getAge() > 100) {
			log.warn("Student registration failed: Invalid age ({}) provided for username: {}",
					registerStudentDTO.getAge(), registerStudentDTO.getUsername());
			throw new RegisterUserException("Invalid age for student");
		}

		if (registerStudentDTO.getSchoolClass() == null || registerStudentDTO.getSchoolClass().isBlank()) {
			log.warn("Student registration failed: School class is missing for username: {}",
					registerStudentDTO.getUsername());
			throw new RegisterUserException("School class is required");
		}

		if (registerStudentDTO.getParentFullName() == null || registerStudentDTO.getParentFullName().isBlank()) {
			log.warn("Student registration failed: Parent full name is missing for username: {}",
					registerStudentDTO.getUsername());
			throw new RegisterUserException("Parent full name is required");
		}

		if (registerStudentDTO.getParentTaxId() == null || registerStudentDTO.getParentTaxId().isBlank()) {
			log.warn("Student registration failed: Parent tax ID is missing for username: {}",
					registerStudentDTO.getUsername());
			throw new RegisterUserException("Parent tax id is required");
		}
	}

}

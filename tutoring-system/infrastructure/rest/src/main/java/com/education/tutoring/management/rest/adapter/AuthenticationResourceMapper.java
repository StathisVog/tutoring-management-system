package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.authentication.AuthenticationResponseDTO;
import com.education.tutoring.management.application.dto.authentication.LoginRequestDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.rest.model.authentication.LoginRequestResource;
import com.education.tutoring.management.rest.model.authentication.LoginResponseResource;
import com.education.tutoring.management.rest.model.authentication.RegisterStudentResource;
import com.education.tutoring.management.rest.model.authentication.RegisterTeacherResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper facilitating the authentication and onboarding workflows. Converts
 * inbound HTTP requests for student/teacher registration and login into internal DTOs,
 * and maps secure application responses back to the client.
 */
@Mapper
public interface AuthenticationResourceMapper {

	/** Singleton instance of the mapper. */
	AuthenticationResourceMapper MAPPER = Mappers.getMapper(AuthenticationResourceMapper.class);

	/**
	 * Maps a RegisterStudentResource to a RegisterStudentDTO.
	 * @param registerStudentResource the resource to be mapped
	 * @return the mapped DTO
	 */
	RegisterStudentDTO toDTO(RegisterStudentResource registerStudentResource);

	/**
	 * Maps a RegisterTeacherResource to a RegisterTeacherDTO.
	 * @param registerTeacherResource the resource to be mapped
	 * @return the mapped DTO
	 */
	RegisterTeacherDTO toDTO(RegisterTeacherResource registerTeacherResource);

	/**
	 * Maps a LoginRequestResource to a LoginRequestDTO.
	 * @param loginRequestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	LoginRequestDTO toDTO(LoginRequestResource loginRequestResource);

	/**
	 * Maps an AuthenticationResponseDTO to a LoginResponseResource.
	 * @param authenticationResponseDTO the DTO to be mapped
	 * @return the mapped resource
	 */
	LoginResponseResource toResource(AuthenticationResponseDTO authenticationResponseDTO);

}

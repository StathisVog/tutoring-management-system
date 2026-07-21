package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.admin.EnrolledStudentDTO;
import com.education.tutoring.management.application.dto.EnrollmentResponseDTO;
import com.education.tutoring.management.rest.model.admin.EnrolledStudentResponseResource;
import com.education.tutoring.management.rest.model.EnrollmentResponseResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper dedicated to student enrollment operations. Converts internal
 * enrollment states and class roster data into clean, formatted REST API responses.
 */
@Mapper
public interface EnrollmentResourceMapper {

	/** Singleton instance of the mapper. */
	EnrollmentResourceMapper MAPPER = Mappers.getMapper(EnrollmentResourceMapper.class);

	/**
	 * Maps an EnrollmentResponseDTO to an EnrollmentResponseResource for API responses.
	 * @param enrollmentResponseDTO the source DTO
	 * @return the mapped Resource
	 */
	EnrollmentResponseResource toEnrollmentResponseResource(EnrollmentResponseDTO enrollmentResponseDTO);

	/**
	 * Maps a list of EnrollmentResponseDTOs to a list of EnrollmentResponseResources.
	 * @param enrollmentResponseDTOList the list of source DTOs
	 * @return the mapped list of Resources
	 */
	List<EnrollmentResponseResource> toEnrollmentResponseResourceList(
			List<EnrollmentResponseDTO> enrollmentResponseDTOList);

	/**
	 * Maps an EnrolledStudentDTO to an EnrolledStudentResponseResource.
	 * @param enrolledStudentDTO the source DTO
	 * @return the mapped resource
	 */
	EnrolledStudentResponseResource toEnrolledStudentResponseResource(EnrolledStudentDTO enrolledStudentDTO);

	/**
	 * Maps a list of EnrolledStudentDTOs to a list of EnrolledStudentResponseResources.
	 * @param enrolledStudentDTOList the list of source DTOs
	 * @return the list of mapped resources
	 */
	List<EnrolledStudentResponseResource> toEnrolledStudentResponseResourceList(
			List<EnrolledStudentDTO> enrolledStudentDTOList);

}

package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.admin.*;
import com.education.tutoring.management.rest.model.admin.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper tailored exclusively for administrative operations. Bridges the Web
 * and Application layers by translating complex, elevated-privilege requests and
 * responses regarding test management, grading, teacher absences, and lesson activity
 * auditing.
 */
@Mapper
public interface AdminResourceMapper {

	/** Singleton instance of the mapper. */
	AdminResourceMapper MAPPER = Mappers.getMapper(AdminResourceMapper.class);

	/**
	 * Maps a list of AdminLessonActivityDTO to a list of
	 * AdminLessonActivityResponseResource
	 * @param adminLessonActivityDTOList the source DTO list
	 * @return the mapped resource list
	 */
	List<AdminLessonActivityResponseResource> toAdminLessonActivityResponseResourceList(
			List<AdminLessonActivityDTO> adminLessonActivityDTOList);

	/**
	 * Maps an AdminUpdateLessonActivityRequestResource to an AdminUpdateLessonActivityDTO
	 * @param requestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	AdminUpdateLessonActivityDTO toAdminUpdateLessonActivityDTO(
			AdminUpdateLessonActivityRequestResource requestResource);

	/**
	 * Maps an AdminTestDTO to a AdminTestResponseResource
	 * @param adminTestDTO the source DTO
	 * @return the mapped resource
	 */
	AdminTestResponseResource toAdminTestResponseResource(AdminTestDTO adminTestDTO);

	/**
	 * Maps a list of AdminTestDTO to a list of AdminTestResponseResource
	 * @param adminTestDTOList the source DTO list
	 * @return the mapped resource list
	 */
	List<AdminTestResponseResource> toAdminTestResponseResourceList(List<AdminTestDTO> adminTestDTOList);

	/**
	 * Maps an AdminTestResultDTO to a AdminTestResultResponseResource
	 * @param adminTestResultDTO the source DTO
	 * @return the mapped resource
	 */
	AdminTestResultResponseResource toAdminTestResultResponseResource(AdminTestResultDTO adminTestResultDTO);

	/**
	 * Maps an AdminUpdateTestResultRequestResource to an AdminUpdateTestResultDTO
	 * @param requestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	AdminUpdateTestResultDTO toAdminUpdateTestResultDTO(AdminUpdateTestResultRequestResource requestResource);

	/**
	 * Maps an AdminUpdateTestRequestResource to an AdminUpdateTestDTO
	 * @param requestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	AdminUpdateTestDTO toAdminUpdateTestDTO(AdminUpdateTestRequestResource requestResource);

	/**
	 * Maps a list of AdminTeacherAbsenceDTO to a list of
	 * AdminTeacherAbsenceResponseResource
	 * @param adminTeacherAbsenceDTOList the source DTO list
	 * @return the mapped resource list
	 */
	List<AdminTeacherAbsenceResponseResource> toAdminTeacherAbsenceResponseResourceList(
			List<AdminTeacherAbsenceDTO> adminTeacherAbsenceDTOList);

	/**
	 * Maps an AdminUpdateTeacherAbsenceRequestResource to an AdminUpdateTeacherAbsenceDTO
	 * @param requestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	AdminUpdateTeacherAbsenceDTO toAdminUpdateTeacherAbsenceDTO(
			AdminUpdateTeacherAbsenceRequestResource requestResource);

}

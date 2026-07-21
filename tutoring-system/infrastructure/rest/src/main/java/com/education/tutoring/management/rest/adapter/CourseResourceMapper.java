package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.dto.course.CourseAssignmentDTO;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.rest.model.course.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for the core educational course domain. Handles the bidirectional
 * translation of course creation payloads, updates, and teacher assignment records
 * between the Web API and the Application layer.
 */
@Mapper
public interface CourseResourceMapper {

	/** Singleton instance of the mapper. */
	CourseResourceMapper MAPPER = Mappers.getMapper(CourseResourceMapper.class);

	/**
	 * Maps a creation payload resource to a CourseDTO
	 * @param createCourseResource the incoming creation payload
	 * @return mapped CourseDTO
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	CourseDTO toCourseDTO(CreateCourseResource createCourseResource);

	/**
	 * Maps a CourseDTO to a CourseResource
	 * @param courseDTO the source DTO
	 * @return the mapped Resource
	 */
	CourseResource toCourseResource(CourseDTO courseDTO);

	/**
	 * Maps an UpdateCourseResource to a CourseDTO
	 * @param updateCourseResource the incoming updating payload
	 * @return mapped DTO
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	CourseDTO toCourseDTO(UpdateCourseResource updateCourseResource);

	/**
	 * Maps a CourseAssignmentDTO to a CourseAssignmentResource
	 * @param courseAssignmentDTO the source DTO
	 * @return the mapped Resource
	 */
	CourseAssignmentResource toAssignmentResource(CourseAssignmentDTO courseAssignmentDTO);

	/**
	 * Maps a list of CourseAssignmentDTO to a list of CourseAssignmentResource
	 * @param courseAssignmentDTOList the source DTO list
	 * @return the mapped Resource list
	 */
	List<CourseAssignmentResource> toAssignmentResourceList(List<CourseAssignmentDTO> courseAssignmentDTOList);

	/**
	 * Maps an AssignedTeacherDTO to an AssignedTeacherResource
	 * @param assignedTeacherDTO the source DTO
	 * @return the mapped Resource
	 */
	AssignedTeacherResource toAssignedTeacherResource(AssignedTeacherDTO assignedTeacherDTO);

	/**
	 * Maps a list of AssignedTeacherDTO to a list of AssignedTeacherResource
	 * @param assignedTeacherDTOList the source DTO list
	 * @return the mapped Resource list
	 */
	List<AssignedTeacherResource> toAssignedTeacherResourceList(List<AssignedTeacherDTO> assignedTeacherDTOList);

}

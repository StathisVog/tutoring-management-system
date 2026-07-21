package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.application.dto.user.TeacherDTO;
import com.education.tutoring.management.application.dto.user.UpdatePasswordDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.rest.model.user.StudentResource;
import com.education.tutoring.management.rest.model.user.TeacherResource;
import com.education.tutoring.management.rest.model.user.UpdatePasswordResource;
import com.education.tutoring.management.rest.model.user.UserResource;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper responsible for converting between User-related Application DTOs and
 * REST Resources. Features advanced polymorphic mapping to seamlessly handle the
 * inheritance tree of Student, Teacher, and base User profiles.
 */
@Mapper
public interface UserResourceMapper {

	/** Singleton instance of the mapper. */
	UserResourceMapper MAPPER = Mappers.getMapper(UserResourceMapper.class);

	/**
	 * Polymorphic mapping method. Determines the specific child type and delegates to the
	 * appropriate mapping method.
	 */
	@Named("polymorphicMapper")
	default UserResource toResource(UserDTO userDTO) {
		if (userDTO == null) {
			return null;
		}

		if (userDTO instanceof StudentDTO studentDTO) {
			return toStudentResource(studentDTO);
		}

		if (userDTO instanceof TeacherDTO teacherDTO) {
			return toTeacherResource(teacherDTO);
		}

		// Fallback for a base UserDTO (e.g., an Admin user)
		return toBaseUserResource(userDTO);
	}

	/**
	 * Maps a StudentDTO to a StudentResource.
	 * @param studentDTO the DTO to be mapped
	 * @return the mapped resource
	 */
	StudentResource toStudentResource(StudentDTO studentDTO);

	/**
	 * Maps a TeacherDTO to a TeacherResource.
	 * @param teacherDTO the DTO to be mapped
	 * @return the mapped resource
	 */
	TeacherResource toTeacherResource(TeacherDTO teacherDTO);

	/**
	 * Maps a UserDTO to a UserResource.
	 * @param userDTO the DTO to be mapped
	 * @return the mapped resource
	 */
	UserResource toBaseUserResource(UserDTO userDTO);

	@IterableMapping(qualifiedByName = "polymorphicMapper")
	List<UserResource> toResources(List<UserDTO> userDTOList);

	/**
	 * Reverse Polymorphic mapping: Resource -> DTO
	 */
	@Named("reversePolymorphicMapper")
	default UserDTO toDTO(UserResource resource) {
		if (resource == null) {
			return null;
		}
		if (resource instanceof StudentResource studentResource) {
			return toStudentDTO(studentResource);
		}
		if (resource instanceof TeacherResource teacherResource) {
			return toTeacherDTO(teacherResource);
		}
		return toBaseUserDTO(resource);
	}

	/**
	 * Maps a StudentResource to a StudentDTO
	 * @param studentResource the resource to be mapped
	 * @return the mapped DTO
	 */
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	StudentDTO toStudentDTO(StudentResource studentResource);

	/**
	 * Maps a TeacherResource to a TeacherDTO
	 * @param teacherResource the resource to be mapped
	 * @return the mapped DTO
	 */
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	TeacherDTO toTeacherDTO(TeacherResource teacherResource);

	/**
	 * Maps a UserResource to a UserDTO
	 * @param userResource the resource to be mapped
	 * @return the mapped DTO
	 */
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	UserDTO toBaseUserDTO(UserResource userResource);

	/**
	 * Maps an UpdatePasswordResource to an UpdatePasswordDTO
	 * @param resource the resource to be mapped
	 * @return the mapped DTO
	 */
	UpdatePasswordDTO toUpdatePasswordDTO(UpdatePasswordResource resource);

}

package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.dto.student.StudentScheduleSlotDTO;
import com.education.tutoring.management.application.dto.student.StudentTestResponseDTO;
import com.education.tutoring.management.rest.model.student.StudentDailyScheduleResponseResource;
import com.education.tutoring.management.rest.model.student.StudentLessonActivityResponseResource;
import com.education.tutoring.management.rest.model.student.StudentScheduleSlotResponseResource;
import com.education.tutoring.management.rest.model.student.StudentTestResponseResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper bridging the Web and Application layers for Student-specific
 * operations. Translates REST API resources to internal DTOs (and vice versa) for student
 * timetables, lesson activities, and test performance histories.
 */
@Mapper
public interface StudentResourceMapper {

	/** Singleton instance of the mapper. */
	StudentResourceMapper MAPPER = Mappers.getMapper(StudentResourceMapper.class);

	/**
	 * Maps a StudentScheduleSlotDTO to a StudentScheduleSlotResponseResource
	 * @param studentScheduleSlotDTO the source DTO
	 * @return the mapped resource
	 */
	StudentScheduleSlotResponseResource toStudentScheduleSlotResponseResource(
			StudentScheduleSlotDTO studentScheduleSlotDTO);

	/**
	 * Maps a StudentDailyScheduleDTO to a StudentDailyScheduleResponseResource
	 * @param studentDailyScheduleDTO the source DTO
	 * @return the mapped resource
	 */
	StudentDailyScheduleResponseResource toStudentDailyScheduleResponseResource(
			StudentDailyScheduleDTO studentDailyScheduleDTO);

	/**
	 * Maps a list of StudentDailyScheduleDTO to a list of
	 * StudentDailyScheduleResponseResource
	 * @param studentDailyScheduleDTOS the resource DTO list
	 * @return the mapped Resource list
	 */
	List<StudentDailyScheduleResponseResource> toStudentDailyScheduleResponseResourceList(
			List<StudentDailyScheduleDTO> studentDailyScheduleDTOS);

	/**
	 * Maps a StudentTestResponseDTO to a StudentTestResponseResource
	 * @param studentTestResponseDTO the source DTO
	 * @return the mapped resource
	 */
	StudentTestResponseResource toStudentTestResponseResource(StudentTestResponseDTO studentTestResponseDTO);

	/**
	 * Maps a list of StudentTestResponseDTO to a list of StudentTestResponseResource
	 * @param studentTestResponseDTOs the resource DTO list
	 * @return the mapped Resource list
	 */
	List<StudentTestResponseResource> toStudentTestResponseResourceList(
			List<StudentTestResponseDTO> studentTestResponseDTOs);

	/**
	 * Maps a list of StudentLessonActivityDTO to a list of
	 * StudentLessonActivityResponseResource
	 * @param studentLessonActivityDTOs the source DTO list
	 * @return the mapped resource list
	 */
	List<StudentLessonActivityResponseResource> toStudentLessonActivityResponseResourceList(
			List<StudentLessonActivityDTO> studentLessonActivityDTOs);

}

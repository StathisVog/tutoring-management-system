package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.teacher.*;
import com.education.tutoring.management.rest.model.teacher.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper bridging the Web and Application layers for Teacher-specific
 * operations. Translates REST API resources to internal DTOs (and vice versa) for daily
 * schedules, absences, lesson activities, and class rosters.
 */
@Mapper
public interface TeacherResourceMapper {

	/** Singleton instance of the mapper. */
	TeacherResourceMapper MAPPER = Mappers.getMapper(TeacherResourceMapper.class);

	/**
	 * Maps a AbsenceResource to a AbsenceRequestDTO
	 * @param absenceResource the resource to be mapped
	 * @return the mapped DTO
	 */
	AbsenceRequestDTO toAbsenceRequestDTO(AbsenceResource absenceResource);

	/**
	 * Maps a TeacherAbsenceDTO to a TeacherAbsenceResponseResource
	 * @param teacherAbsenceDTO the source DTO
	 * @return the mapped resource
	 */
	TeacherAbsenceResponseResource toTeacherAbsenceResponseResource(TeacherAbsenceDTO teacherAbsenceDTO);

	/**
	 * Maps a TeacherScheduleSlotDTO to a TeacherScheduleSlotResponseResource
	 * @param teacherScheduleSlotDTO the source DTO
	 * @return the mapped resource
	 */
	TeacherScheduleSlotResponseResource toTeacherScheduleSlotResponseResource(
			TeacherScheduleSlotDTO teacherScheduleSlotDTO);

	/**
	 * Maps a TeacherDailyScheduleDTO to a TeacherDailyScheduleResponseResource
	 * @param dailyScheduleDTO the source DTO
	 * @return the mapped resource
	 */
	TeacherDailyScheduleResponseResource toTeacherDailyScheduleResponseResource(
			TeacherDailyScheduleDTO dailyScheduleDTO);

	/**
	 * Maps a list of TeacherDailyScheduleDTO to a list of
	 * TeacherDailyScheduleResponseResource
	 * @param dailyScheduleDTOs the source DTO list
	 * @return the mapped resource list
	 */
	List<TeacherDailyScheduleResponseResource> toTeacherDailyScheduleResponseResourceList(
			List<TeacherDailyScheduleDTO> dailyScheduleDTOs);

	/**
	 * Maps a CreateLessonActivityResource to a CreateLessonActivityDTO
	 * @param createLessonActivityResource the resource to be mapped
	 * @return the mapped DTO
	 */
	CreateLessonActivityDTO toCreateLessonActivityDTO(CreateLessonActivityResource createLessonActivityResource);

	/**
	 * Maps a list of LessonActivityDTO to a LessonActivityResponseResource list
	 * @param lessonActivityDTOs the source DTO list
	 * @return the mapped resource list
	 */
	List<LessonActivityResponseResource> toLessonActivityResponseResourceList(
			List<LessonActivityDTO> lessonActivityDTOs);

	/**
	 * Maps an UpdateLessonActivityResource to an UpdateLessonActivityDTO
	 * @param updateLessonActivityResource the resource to be mapped
	 * @return the mapped DTO
	 */
	UpdateLessonActivityDTO toUpdateLessonActivityDTO(UpdateLessonActivityResource updateLessonActivityResource);

	/**
	 * Maps an ActiveStudentDTO to an ActiveStudentResource
	 * @param activeStudentDTO the source DTO
	 * @return the mapped resource
	 */
	ActiveStudentResource toActiveStudentResource(ActiveStudentDTO activeStudentDTO);

	/**
	 * Maps a list of ActiveStudentDTO to a list of ActiveStudentResource
	 * @param activeStudentDTOs the source DTO list
	 * @return the mapped resource list
	 */
	List<ActiveStudentResource> toActiveStudentResourceList(List<ActiveStudentDTO> activeStudentDTOs);

}

package com.education.tutoring.management.repository.jpa.mapper;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.admin.AdminLessonActivityDTO;
import com.education.tutoring.management.application.dto.admin.AdminTeacherAbsenceDTO;
import com.education.tutoring.management.application.dto.admin.AdminTestDTO;
import com.education.tutoring.management.application.dto.admin.AdminTestResultDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterStudentDTO;
import com.education.tutoring.management.application.dto.authentication.RegisterTeacherDTO;
import com.education.tutoring.management.application.dto.course.AssignedTeacherDTO;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.dto.student.StudentLessonActivityDTO;
import com.education.tutoring.management.application.dto.teacher.*;
import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.application.dto.user.TeacherDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.util.SchoolClass;
import com.education.tutoring.management.repository.jpa.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper
public interface EntityMapper {

	/** Singleton instance of the mapper. */
	EntityMapper MAPPER = Mappers.getMapper(EntityMapper.class);

	// ======================================================
	// COURSE - ENROLLMENT - LESSON - SCHEDULED-SLOT MAPPINGS
	// ======================================================

	/**
	 * Converts a Course entity to a CourseDTO
	 * @param course the source entity
	 * @return the destination DTO
	 */
	CourseDTO toCourseDTO(Course course);

	/**
	 * Converts a CourseDTO to a Course entity.
	 * @param courseDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "scheduledSlots", ignore = true)
	@Mapping(target = "tests", ignore = true)
	Course toCourseEntity(CourseDTO courseDTO);

	/**
	 * Converts an Enrollment entity to an EnrollmentDTO.
	 * @param enrollment the source entity
	 * @return the destination DTO
	 */
	EnrollmentDTO toEnrollmentDTO(Enrollment enrollment);

	/**
	 * Converts an EnrollmentDTO to an Enrollment entity.
	 * @param enrollmentDTO the source DTO
	 * @return the destination entity
	 */
	Enrollment toEnrollmentEntity(EnrollmentDTO enrollmentDTO);

	/**
	 * Converts an Enrollment entity to an EnrollmentResponseDTO
	 * @param enrollment the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "enrollmentId")
	@Mapping(source = "scheduledSlot.id", target = "scheduledSlotId")
	@Mapping(source = "scheduledSlot.course.title", target = "courseTitle")
	@Mapping(source = "scheduledSlot.teacher.fullName", target = "teacherName")
	@Mapping(source = "scheduledSlot.dayOfWeek", target = "dayOfWeek")
	@Mapping(source = "scheduledSlot.startTime", target = "startTime")
	@Mapping(source = "scheduledSlot.endTime", target = "endTime")
	@Mapping(source = "scheduledSlot.classroom", target = "classroom")
	@Mapping(source = "student.id", target = "studentId")
	@Mapping(source = "student.fullName", target = "studentFullName")
	@Mapping(source = "student.email", target = "studentEmail")
	EnrollmentResponseDTO toEnrollmentResponseDTO(Enrollment enrollment);

	/**
	 * Converts a LessonActivity entity to a LessonActivityDTO.
	 * @param lessonActivity the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(source = "scheduledSlot.course.title", target = "courseTitle")
	LessonActivityDTO toLessonActivityDTO(LessonActivity lessonActivity);

	/**
	 * Converts a LessonActivityDTO to a LessonActivity entity.
	 * @param lessonActivityDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "scheduledSlot", ignore = true)
	LessonActivity toLessonActivityEntity(LessonActivityDTO lessonActivityDTO);

	/**
	 * Maps a LessonActivity entity to a StudentLessonActivityDTO.
	 * @param lessonActivity the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "activityId")
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(source = "scheduledSlot.course.title", target = "courseTitle")
	@Mapping(source = "scheduledSlot.teacher.fullName", target = "teacherFullName")
	StudentLessonActivityDTO toStudentLessonActivityDTO(LessonActivity lessonActivity);

	/**
	 * Maps a LessonActivity entity to a AdminLessonActivityDTO.
	 * @param lessonActivity the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "activityId")
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(source = "scheduledSlot.course.id", target = "courseId")
	@Mapping(source = "scheduledSlot.course.title", target = "courseTitle")
	@Mapping(source = "scheduledSlot.teacher.id", target = "teacherId")
	@Mapping(source = "scheduledSlot.teacher.fullName", target = "teacherFullName")
	AdminLessonActivityDTO toAdminLessonActivityDTO(LessonActivity lessonActivity);

	/**
	 * Converts a ScheduledSlot entity to a ScheduledSlotDTO.
	 * @param scheduledSlot the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "course.id", target = "courseId")
	@Mapping(source = "course.title", target = "courseTitle")
	@Mapping(source = "teacher.id", target = "teacherId")
	@Mapping(source = "teacher.fullName", target = "teacherName")
	@Mapping(target = "availableSeats", ignore = true)
	ScheduledSlotDTO toScheduledSlotDTO(ScheduledSlot scheduledSlot);

	/**
	 * Converts a ScheduledSlotDTO to a ScheduledSlot entity.
	 * @param scheduledSlotDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "lessonActivities", ignore = true)
	@Mapping(source = "courseId", target = "course.id")
	@Mapping(source = "teacherId", target = "teacher.id")
	ScheduledSlot toScheduledSlotEntity(ScheduledSlotDTO scheduledSlotDTO);

	// ====================================
	// POLYMORPHIC USER MAPPINGS (THE CORE)
	// ====================================

	/**
	 * Converts a User entity to a UserDTO, handling polymorphic subtypes (Student,
	 * Teacher). This acts as the central router for all User entities coming from the DB.
	 * @param user the source entity
	 * @return the destination polymorphic DTO
	 */
	@Named("polymorphicEntityMapper")
	default UserDTO toUserDTO(User user) {
		if (user == null) {
			return null;
		}

		if (user instanceof Student student) {
			return toStudentDTO(student);
		}

		if (user instanceof Teacher teacher) {
			return toTeacherDTO(teacher);
		}

		return mapBaseUserToUserDTO(user);
	}

	/**
	 * Fallback mapping for a base User entity (e.g., an Admin).
	 */
	UserDTO mapBaseUserToUserDTO(User user);

	/**
	 * Converts a Student entity to a StudentDTO.
	 * @param student the source entity
	 * @return the destination DTO
	 */
	StudentDTO toStudentDTO(Student student);

	/**
	 * Converts a Student entity to an ActiveStudentDTO.
	 * @param student the source entity
	 * @return the destination DTO
	 */
	ActiveStudentDTO toActiveStudentDTO(Student student);

	/**
	 * Converts a StudentDTO to a Student entity.
	 * @param studentDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "securityQuestion", ignore = true)
	@Mapping(target = "securityAnswerHash", ignore = true)
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "testResults", ignore = true)
	Student toStudentEntity(StudentDTO studentDTO);

	/**
	 * Converts a Teacher entity to a TeacherDTO.
	 * @param teacher the source entity
	 * @return the destination DTO
	 */
	@Mapping(target = "courses", source = "courses", qualifiedByName = "mapCoursesToTitles")
	@Mapping(target = "eligibleCourses", source = "eligibleCourses", qualifiedByName = "mapCoursesToTitles")
	TeacherDTO toTeacherDTO(Teacher teacher);

	/**
	 * Converts a TeacherDTO to a Teacher entity.
	 * @param teacherDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "securityQuestion", ignore = true)
	@Mapping(target = "securityAnswerHash", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "eligibleCourses", ignore = true)
	@Mapping(target = "scheduledSlots", ignore = true)
	@Mapping(target = "teacherAbsences", ignore = true)
	@Mapping(target = "tests", ignore = true)
	Teacher toTeacherEntity(TeacherDTO teacherDTO);

	/**
	 * Converts a Teacher entity to an AssignedTeacherDTO
	 * @param teacher the source entity
	 * @return the destination DTO
	 */
	AssignedTeacherDTO toAssignedTeacherDTO(Teacher teacher);

	// =====================
	// REGISTRATION MAPPINGS
	// =====================

	/**
	 * Converts a RegisterUserRequestDTO to a Student entity for registration.
	 * @param registerStudentDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "securityQuestion", ignore = true)
	@Mapping(target = "securityAnswerHash", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "testResults", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "themeColor", ignore = true)
	@Mapping(target = "avatarName", ignore = true)
	Student toStudentEntityFromRegister(RegisterStudentDTO registerStudentDTO);

	/**
	 * Converts a RegisterUserRequestDTO to a Teacher entity for registration.
	 * @param registerTeacherDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "securityQuestion", ignore = true)
	@Mapping(target = "securityAnswerHash", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "eligibleCourses", ignore = true)
	@Mapping(target = "scheduledSlots", ignore = true)
	@Mapping(target = "teacherAbsences", ignore = true)
	@Mapping(target = "tests", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "themeColor", ignore = true)
	@Mapping(target = "avatarName", ignore = true)
	Teacher toTeacherEntityFromRegister(RegisterTeacherDTO registerTeacherDTO);

	// ======================================
	// MISC MAPPINGS (ABSENCES, TESTS, ENUMS)
	// ======================================

	/**
	 * Converts a TeacherAbsence entity to a TeacherAbsenceDTO.
	 * @param teacherAbsence the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	TeacherAbsenceDTO toTeacherAbsenceDTO(TeacherAbsence teacherAbsence);

	/**
	 * Converts a TeacherAbsenceDTO to a TeacherAbsence entity.
	 * @param teacherAbsenceDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(target = "scheduledSlot", ignore = true)
	TeacherAbsence toTeacherAbsenceEntity(TeacherAbsenceDTO teacherAbsenceDTO);

	/**
	 * Converts a TeacherAbsence entity to an AdminTeacherAbsenceDTO.
	 * @param teacherAbsence the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "absenceId")
	@Mapping(source = "teacher.id", target = "teacherId")
	@Mapping(source = "teacher.fullName", target = "teacherFullName")
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(source = "scheduledSlot.course.title", target = "courseTitle")
	AdminTeacherAbsenceDTO toAdminTeacherAbsenceDTO(TeacherAbsence teacherAbsence);

	/**
	 * Converts a Test entity to a TestDTO.
	 * @param test the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "course.id", target = "courseId")
	@Mapping(source = "course.title", target = "courseTitle")
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(target = "totalStudentsCount", ignore = true)
	@Mapping(target = "gradedStudentsCount", ignore = true)
	TestDTO toTestDTO(Test test);

	/**
	 * Converts a Test entity to a AdminTestDTO.
	 * @param test the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "testId")
	@Mapping(source = "course.id", target = "courseId")
	@Mapping(source = "course.title", target = "courseTitle")
	@Mapping(source = "scheduledSlot.id", target = "slotId")
	@Mapping(source = "author.id", target = "teacherId")
	@Mapping(source = "author.fullName", target = "teacherFullName")
	@Mapping(source = "testResults", target = "results")
	AdminTestDTO toAdminTestDTO(Test test);

	/**
	 * Converts a TestDTO to a Test entity.
	 * @param testDTO the source DTO
	 * @return the destination entity
	 */
	@Mapping(source = "courseId", target = "course.id")
	@Mapping(target = "testResults", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "scheduledSlot", ignore = true)
	Test toTestEntity(TestDTO testDTO);

	/**
	 * Converts a TestResult entity to a TestResultDTO.
	 * @param testResult the source entity
	 * @return the destination DTO
	 */
	TestResultDTO toTestResultDTO(TestResult testResult);

	/**
	 * Converts a TestResultDTO to a TestResult entity.
	 * @param testResultDTO the source DTO
	 * @return the destination entity
	 */
	TestResult toTestResultEntity(TestResultDTO testResultDTO);

	/**
	 * Converts a TestResult entity to a AdminTestResultDTO.
	 * @param testResult the source entity
	 * @return the destination DTO
	 */
	@Mapping(source = "id", target = "testResultId")
	@Mapping(source = "student.id", target = "studentId")
	@Mapping(source = "student.fullName", target = "studentFullName")
	AdminTestResultDTO toAdminTestResultDTO(TestResult testResult);

	/**
	 * Maps the SchoolClass enum to its human-readable string value for DTOs.
	 */
	default String mapToString(SchoolClass schoolClass) {
		return schoolClass == null ? null : schoolClass.value();
	}

	/**
	 * Maps a string value from a DTO to the SchoolClass enum using our custom logic.
	 */
	default SchoolClass mapToSchoolClass(String value) {
		if (value == null) {
			return null;
		}
		return SchoolClass.fromValue(value);
	}

	/**
	 * Helper method: Converts a Set of Course entities into a List of Course titles.
	 */
	@Named("mapCoursesToTitles")
	default List<String> mapCoursesToTitles(Set<Course> courses) {
		if (courses == null || courses.isEmpty()) {
			return Collections.emptyList();
		}
		return courses.stream().map(Course::getTitle).toList();
	}

}

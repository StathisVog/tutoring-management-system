package com.education.tutoring.management.repository.jpa.mapper;

import com.education.tutoring.management.application.dto.*;
import com.education.tutoring.management.application.dto.course.CourseDTO;
import com.education.tutoring.management.application.dto.teacher.LessonActivityDTO;
import com.education.tutoring.management.application.dto.teacher.TeacherAbsenceDTO;
import com.education.tutoring.management.application.dto.teacher.TestDTO;
import com.education.tutoring.management.application.dto.teacher.TestResultDTO;
import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.application.dto.user.TeacherDTO;
import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.repository.jpa.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;

import static com.education.tutoring.management.domain.util.Role.STUDENT;
import static com.education.tutoring.management.domain.util.SchoolClass.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMapperTest {

	private final EntityMapper mapper = EntityMapper.MAPPER;

	@Test
	@DisplayName("Map Course entity to CourseDTO")
	void mapCourseToCourseDTO() {
		Course course = Course.builder()
			.id(1L)
			.title("Mathematics")
			.description("Preparation for National Exams")
			.gradeLevel("3rd year of high school")
			.active(true)
			.build();

		CourseDTO courseDTO = mapper.toCourseDTO(course);

		assertAll(() -> assertEquals(1L, courseDTO.getId()), () -> assertEquals("Mathematics", courseDTO.getTitle()),
				() -> assertEquals("Preparation for National Exams", courseDTO.getDescription()),
				() -> assertEquals("3rd year of high school", courseDTO.getGradeLevel()),
				() -> assertTrue(courseDTO.isActive()));
	}

	@Test
	@DisplayName("Map CourseDTO to Course entity")
	void mapCourseDTOToCourse() {
		CourseDTO courseDTO = new CourseDTO();

		courseDTO.setId(10L);
		courseDTO.setTitle("Physics");
		courseDTO.setDescription("Preparation for the third year of high school");
		courseDTO.setGradeLevel("2rd year of high school");
		courseDTO.setActive(true);

		Course course = mapper.toCourseEntity(courseDTO);

		assertAll(() -> assertEquals(10L, course.getId()), () -> assertEquals("Physics", course.getTitle()),
				() -> assertEquals("Preparation for the third year of high school", course.getDescription()),
				() -> assertEquals("2rd year of high school", course.getGradeLevel()),
				() -> assertTrue(course.isActive()));
	}

	@Test
	@DisplayName("Map Enrollment entity to EnrollmentDTO")
	void mapEnrollmentToEnrollmentDTO() {
		Enrollment enrollment = Enrollment.builder()
			.id(1L)
			.enrollmentDate(LocalDate.of(2026, Month.JANUARY, 19))
			.status(EnrollmentStatus.ACTIVE)
			.build();

		EnrollmentDTO enrollmentDTO = mapper.toEnrollmentDTO(enrollment);

		assertAll(() -> assertEquals(1L, enrollmentDTO.getId()),
				() -> assertEquals(enrollment.getEnrollmentDate(), enrollmentDTO.getEnrollmentDate()),
				() -> assertEquals(EnrollmentStatus.ACTIVE, enrollmentDTO.getStatus()));
	}

	@Test
	@DisplayName("Map EnrollmentDTO to Enrollment entity")
	void mapEnrollmentDTOToEnrollment() {
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();

		enrollmentDTO.setId(10L);
		enrollmentDTO.setEnrollmentDate(LocalDate.of(2026, Month.MARCH, 9));
		enrollmentDTO.setStatus(EnrollmentStatus.ACTIVE);

		Enrollment enrollment = mapper.toEnrollmentEntity(enrollmentDTO);

		assertAll(() -> assertEquals(10L, enrollment.getId()),
				() -> assertEquals(enrollmentDTO.getEnrollmentDate(), enrollment.getEnrollmentDate()),
				() -> assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus()));
	}

	@Test
	@DisplayName("Map LessonActivity entity to LessonActivityDTO")
	void mapLessonActivityToLessonActivityDTO() {
		LessonActivity lessonActivity = LessonActivity.builder()
			.id(1L)
			.date(LocalDate.of(2026, Month.FEBRUARY, 2))
			.description("Limits and continuity of functions")
			.build();

		LessonActivityDTO lessonActivityDTO = mapper.toLessonActivityDTO(lessonActivity);

		assertAll(() -> assertEquals(1L, lessonActivityDTO.getId()),
				() -> assertEquals(lessonActivity.getDate(), lessonActivityDTO.getDate()),
				() -> assertEquals("Limits and continuity of functions", lessonActivityDTO.getDescription()));
	}

	@Test
	@DisplayName("Map LessonActivityDTO to LessonActivity entity")
	void mapLessonActivityDTOToLessonActivity() {
		LessonActivityDTO lessonActivityDTO = LessonActivityDTO.builder()
			.id(10L)
			.date(LocalDate.of(2026, Month.FEBRUARY, 9))
			.description("Limits and Continuity of Real Functions")
			.build();

		LessonActivity lessonActivity = mapper.toLessonActivityEntity(lessonActivityDTO);

		assertAll(() -> assertEquals(10L, lessonActivity.getId()),
				() -> assertEquals(lessonActivityDTO.getDate(), lessonActivity.getDate()),
				() -> assertEquals("Limits and Continuity of Real Functions", lessonActivity.getDescription()));
	}

	@Test
	@DisplayName("Map ScheduledSlot entity to ScheduledSlotDTO")
	void mapScheduledSlotToScheduledSlotDTO() {
		ScheduledSlot scheduledSlot = ScheduledSlot.builder()
			.id(1L)
			.dayOfWeek(DayOfWeek.MONDAY)
			.startTime(LocalTime.of(17, 0))
			.endTime(LocalTime.of(19, 0))
			.classroom("Room 3")
			.capacity(20)
			.build();

		ScheduledSlotDTO scheduledSlotDTO = mapper.toScheduledSlotDTO(scheduledSlot);

		assertAll(() -> assertEquals(1L, scheduledSlotDTO.getId()),
				() -> assertEquals(DayOfWeek.MONDAY, scheduledSlotDTO.getDayOfWeek()),
				() -> assertEquals(LocalTime.of(17, 0), scheduledSlotDTO.getStartTime()),
				() -> assertEquals(LocalTime.of(19, 0), scheduledSlotDTO.getEndTime()),
				() -> assertEquals("Room 3", scheduledSlotDTO.getClassroom()),
				() -> assertEquals(20L, scheduledSlotDTO.getCapacity()));
	}

	@Test
	@DisplayName("Map ScheduledSlotDTO to ScheduledSlot entity")
	void mapScheduledSlotDTOToScheduledSlot() {
		ScheduledSlotDTO scheduledSlotDTO = new ScheduledSlotDTO();

		scheduledSlotDTO.setId(10L);
		scheduledSlotDTO.setDayOfWeek(DayOfWeek.TUESDAY);
		scheduledSlotDTO.setStartTime(LocalTime.of(19, 0));
		scheduledSlotDTO.setEndTime(LocalTime.of(21, 0));
		scheduledSlotDTO.setClassroom("Room 2");
		scheduledSlotDTO.setCapacity(15);

		ScheduledSlot scheduledSlot = mapper.toScheduledSlotEntity(scheduledSlotDTO);

		assertAll(() -> assertEquals(10L, scheduledSlot.getId()),
				() -> assertEquals(DayOfWeek.TUESDAY, scheduledSlot.getDayOfWeek()),
				() -> assertEquals(LocalTime.of(19, 0), scheduledSlot.getStartTime()),
				() -> assertEquals(LocalTime.of(21, 0), scheduledSlot.getEndTime()),
				() -> assertEquals("Room 2", scheduledSlot.getClassroom()),
				() -> assertEquals(15L, scheduledSlot.getCapacity()));
	}

	@Test
	@DisplayName("Map Student entity to StudentDTO")
	void mapStudentToStudentDTO() {
		Student student = Student.builder()
			.age(15)
			.schoolClass(A_LUKEIOU)
			.parentFullName("Giannis Papadopoulos")
			.parentTaxId("123456789")
			.build();

		StudentDTO studentDTO = mapper.toStudentDTO(student);

		assertAll(() -> assertEquals(15, studentDTO.getAge()),
				() -> assertEquals(A_LUKEIOU, studentDTO.getSchoolClass()),
				() -> assertEquals("Giannis Papadopoulos", studentDTO.getParentFullName()),
				() -> assertEquals("123456789", studentDTO.getParentTaxId()));
	}

	@Test
	@DisplayName("Map StudentDTO to Student entity")
	void mapStudentDTOToStudent() {
		StudentDTO studentDTO = new StudentDTO();

		studentDTO.setId(10L);
		studentDTO.setUsername("nikosg17");
		studentDTO.setEmail("nikosgiannakis17@example.com");
		studentDTO.setFullName("Nikos Giannakis");
		studentDTO.setAge(17);
		studentDTO.setSchoolClass(C_LUKEIOU);
		studentDTO.setParentFullName("Petros Giannakis");
		studentDTO.setParentTaxId("123456789");

		Student student = mapper.toStudentEntity(studentDTO);

		assertAll(() -> assertEquals(10L, student.getId()), () -> assertEquals("nikosg17", student.getUsername()),
				() -> assertEquals("nikosgiannakis17@example.com", student.getEmail()),
				() -> assertEquals("Nikos Giannakis", student.getFullName()), () -> assertEquals(17, student.getAge()),
				() -> assertEquals(C_LUKEIOU, student.getSchoolClass()),
				() -> assertEquals("Petros Giannakis", student.getParentFullName()),
				() -> assertEquals("123456789", student.getParentTaxId()));
	}

	@Test
	@DisplayName("Map TeacherAbsence entity to TeacherAbsenceDTO")
	void mapTeacherAbsenceToTeacherAbsenceDTO() {
		TeacherAbsence teacherAbsence = TeacherAbsence.builder()
			.id(1L)
			.date(LocalDate.of(2026, Month.APRIL, 10))
			.reason("Illness")
			.build();

		TeacherAbsenceDTO teacherAbsenceDTO = mapper.toTeacherAbsenceDTO(teacherAbsence);

		assertAll(() -> assertEquals(1L, teacherAbsenceDTO.getId()),
				() -> assertEquals(LocalDate.of(2026, Month.APRIL, 10), teacherAbsenceDTO.getDate()),
				() -> assertEquals("Illness", teacherAbsenceDTO.getReason()));
	}

	@Test
	@DisplayName("Map TeacherAbsenceDTO to TeacherAbsence entity")
	void mapTeacherAbsenceDTOToTeacherAbsence() {
		TeacherAbsenceDTO teacherAbsenceDTO = new TeacherAbsenceDTO();

		teacherAbsenceDTO.setId(10L);
		teacherAbsenceDTO.setDate(LocalDate.of(2026, Month.MAY, 6));
		teacherAbsenceDTO.setReason("Sickness");

		TeacherAbsence teacherAbsence = mapper.toTeacherAbsenceEntity(teacherAbsenceDTO);

		assertAll(() -> assertEquals(10L, teacherAbsence.getId()),
				() -> assertEquals(LocalDate.of(2026, Month.MAY, 6), teacherAbsence.getDate()),
				() -> assertEquals("Sickness", teacherAbsence.getReason()));
	}

	@Test
	@DisplayName("Map Teacher entity to TeacherDTO")
	void mapTeacherToTeacherDTO() {
		Teacher teacher = Teacher.builder()
			.specialty("Math Educator")
			.bio("Math Educator with 10 years of experience in National Entrance Exams.")
			.build();

		TeacherDTO teacherDTO = mapper.toTeacherDTO(teacher);

		assertAll(() -> assertEquals("Math Educator", teacherDTO.getSpecialty()),
				() -> assertEquals("Math Educator with 10 years of experience in National Entrance Exams.",
						teacherDTO.getBio()));
	}

	@Test
	@DisplayName("Map TeacherDTO to Teacher entity")
	void mapTeacherDTOToTeacher() {
		TeacherDTO teacherDTO = new TeacherDTO();

		teacherDTO.setId(10L);
		teacherDTO.setUsername("MathEducator1");
		teacherDTO.setEmail("mathEducator1@example.com");
		teacherDTO.setFullName("Konstantinos Ioannou");
		teacherDTO.setSpecialty("Philologist & Composition Tutor");
		teacherDTO.setBio("Dedicated Modern Greek Language and Composition Educator with over 8 years of experience.");

		Teacher teacher = mapper.toTeacherEntity(teacherDTO);

		assertAll(() -> assertEquals(10L, teacher.getId()), () -> assertEquals("MathEducator1", teacher.getUsername()),
				() -> assertEquals("mathEducator1@example.com", teacher.getEmail()),
				() -> assertEquals("Konstantinos Ioannou", teacher.getFullName()),
				() -> assertEquals("Philologist & Composition Tutor", teacher.getSpecialty()),
				() -> assertEquals(
						"Dedicated Modern Greek Language and Composition Educator with over 8 years of experience.",
						teacher.getBio()));
	}

	@Test
	@DisplayName("Map Test entity to TestDTO")
	void mapTestToTestDTO() {
		com.education.tutoring.management.repository.jpa.entity.Test test = com.education.tutoring.management.repository.jpa.entity.Test
			.builder()
			.id(1L)
			.date(LocalDate.of(2026, Month.FEBRUARY, 20))
			.description("Chapters 1 & 2 Test")
			.build();

		TestDTO testDTO = mapper.toTestDTO(test);

		assertAll(() -> assertEquals(1L, testDTO.getId()),
				() -> assertEquals(LocalDate.of(2026, Month.FEBRUARY, 20), testDTO.getDate()),
				() -> assertEquals("Chapters 1 & 2 Test", testDTO.getDescription()));
	}

	@Test
	@DisplayName("Map TestDTO to Test entity")
	void mapTestDTOToTest() {
		TestDTO testDTO = new TestDTO();

		testDTO.setId(10L);
		testDTO.setDate(LocalDate.of(2026, Month.FEBRUARY, 27));
		testDTO.setDescription("Chapters 3 & 4 Test");

		com.education.tutoring.management.repository.jpa.entity.Test test = mapper.toTestEntity(testDTO);

		assertAll(() -> assertEquals(10L, test.getId()),
				() -> assertEquals(LocalDate.of(2026, Month.FEBRUARY, 27), test.getDate()),
				() -> assertEquals("Chapters 3 & 4 Test", test.getDescription()));
	}

	@Test
	@DisplayName("Map TestResult entity to TestResultDTO")
	void mapTestResultToTestResultDTO() {
		TestResult testResult = TestResult.builder().id(1L).grade(BigDecimal.valueOf(18.5)).build();

		TestResultDTO testResultDTO = mapper.toTestResultDTO(testResult);

		assertAll(() -> assertEquals(1L, testResultDTO.getId()),
				() -> assertEquals(BigDecimal.valueOf(18.5), testResultDTO.getGrade()));
	}

	@Test
	@DisplayName("Map TestResultDTO to TestResult entity")
	void mapTestResultDTOToTestResult() {
		TestResultDTO testResultDTO = new TestResultDTO();

		testResultDTO.setId(10L);
		testResultDTO.setGrade(BigDecimal.valueOf(20));

		TestResult testResult = mapper.toTestResultEntity(testResultDTO);

		assertAll(() -> assertEquals(10L, testResult.getId()),
				() -> assertEquals(BigDecimal.valueOf(20), testResult.getGrade()));
	}

	@Test
	@DisplayName("Map Student entity to UserDTO")
	void mapStudentToUserDTO() {
		Student student = Student.builder()
			.id(1L)
			.username("steliosChor16")
			.email("steliosChorianopoulos@example.com")
			.fullName("Stelios Chorianopoulos")
			.address("Athens, Nea Smyrni")
			.role(STUDENT)
			.enabled(true)
			.createdAt(LocalDateTime.of(2025, Month.SEPTEMBER, 1, 18, 0))
			.updatedAt(LocalDateTime.of(2026, Month.JANUARY, 12, 10, 0))
			.age(16)
			.schoolClass(B_LUKEIOU)
			.parentFullName("Dimitris Chorianopoulos")
			.parentTaxId("102030405")
			.build();

		UserDTO userDTO = mapper.toUserDTO(student);

		assertAll(() -> assertEquals(1L, userDTO.getId()), () -> assertEquals("steliosChor16", userDTO.getUsername()),
				() -> assertEquals("steliosChorianopoulos@example.com", userDTO.getEmail()),
				() -> assertEquals("Stelios Chorianopoulos", userDTO.getFullName()),
				() -> assertEquals("Athens, Nea Smyrni", userDTO.getAddress()),
				() -> assertEquals(STUDENT, userDTO.getRole()), () -> assertTrue(userDTO.isEnabled()),
				() -> assertEquals(LocalDateTime.of(2025, Month.SEPTEMBER, 1, 18, 0), userDTO.getCreatedAt()),
				() -> assertEquals(LocalDateTime.of(2026, Month.JANUARY, 12, 10, 0), userDTO.getUpdatedAt()));
	}

}

package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.teacher.CreateTestRequestDTO;
import com.education.tutoring.management.application.dto.teacher.GradeTestResultDTO;
import com.education.tutoring.management.application.dto.teacher.TestDTO;
import com.education.tutoring.management.application.dto.teacher.TestResultDTO;
import com.education.tutoring.management.rest.model.teacher.CreateTestResource;
import com.education.tutoring.management.rest.model.teacher.GradeTestResultResource;
import com.education.tutoring.management.rest.model.teacher.TeachersTestResultResource;
import com.education.tutoring.management.rest.model.teacher.TestResponseResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper responsible for translating Test-related data objects between the Web
 * and Application layers. Handles the conversion of resources and DTOs for test creation,
 * grading workflows, and result retrieval.
 */
@Mapper
public interface TestResourceMapper {

	/** Singleton instance of the mapper. */
	TestResourceMapper MAPPER = Mappers.getMapper(TestResourceMapper.class);

	/**
	 * Maps a CreateTestResource to a CreateTestRequestDTO
	 * @param createTestResource the resource to be mapped
	 * @return the mapped DTO
	 */
	CreateTestRequestDTO toCreateTestRequestDTO(CreateTestResource createTestResource);

	/**
	 * Maps a TestDTO to a TestResponseResource
	 * @param testDTO the resource DTO
	 * @return the mapped resource
	 */
	TestResponseResource toTestResponseResource(TestDTO testDTO);

	/**
	 * Maps a TestResultDTO to a TeachersTestResultResource
	 * @param testResultDTO the resource DTO
	 * @return the mapped resource
	 */
	@Mapping(source = "id", target = "testResultId")
	@Mapping(source = "student.fullName", target = "studentFullName")
	TeachersTestResultResource toTeachersTestResultResource(TestResultDTO testResultDTO);

	/**
	 * Maps a GradeTestResultResource to a GradeTestResultDTO
	 * @param gradeTestResultResource the resource to be mapped
	 * @return the mapped DTO
	 */
	GradeTestResultDTO toGradeTestResultDTO(GradeTestResultResource gradeTestResultResource);

}

package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API response representing a test created by a teacher, including aggregated
 * grading statistics.
 */
@Data
@Schema(description = "Represents a test created by a teacher")
public class TestResponseResource {

	@Schema(description = "The unique identifier of the test", example = "1")
	private Long id;

	@Schema(description = "The unique identifier of the course", example = "1")
	private Long courseId;

	@Schema(description = "The human-readable title of the course",
			example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The unique identifier of the scheduled slot (Class)", example = "1")
	private Long slotId;

	@Schema(description = "The date the test is scheduled for", example = "2026-05-28")
	private LocalDate date;

	@Schema(description = "The description or syllabus of the test",
			example = "Test 1: Chemical Equilibrium & Acid-Base Theories")
	private String description;

	@Schema(description = "Total number of students assigned to this test", example = "15")
	private int totalStudentsCount;

	@Schema(description = "Number of students who have received a grade", example = "12")
	private int gradedStudentsCount;

}

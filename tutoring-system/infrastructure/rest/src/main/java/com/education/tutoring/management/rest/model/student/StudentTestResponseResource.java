package com.education.tutoring.management.rest.model.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST API response representing a test assigned to a student, along with their
 * individual grade and teacher feedback.
 */
@Data
@Schema(description = "Details of a test assigned to the student, including their personal result if graded")
public class StudentTestResponseResource {

	@Schema(description = "The unique ID of the test result record", example = "1")
	private Long testResultId;

	@Schema(description = "The name of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseName;

	@Schema(description = "The full name of the teacher who created the test", example = "Dimitris Ioannidis")
	private String teacherName;

	@Schema(description = "The date the test is scheduled for", example = "2026-05-25")
	private LocalDate date;

	@Schema(description = "The description or title of the test",
			example = "Test 1: Chemical Equilibrium & Acid-Base Theories")
	private String description;

	@Schema(description = "The student's grade. Null if the test has not been graded yet.", example = "18.5")
	private BigDecimal grade;

	@Schema(description = "Optional feedback comments from the teacher", example = "Great effort!")
	private String comments;

	@Schema(description = "The current status of the test for the UI", example = "PENDING")
	private String status;

}

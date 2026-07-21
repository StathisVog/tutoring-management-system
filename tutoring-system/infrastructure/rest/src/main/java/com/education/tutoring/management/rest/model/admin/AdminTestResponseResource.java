package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * REST resource representing test details and all associated student results for
 * administrative API responses.
 */
@Data
@Schema(description = "Comprehensive details of a test, including all student results")
public class AdminTestResponseResource {

	@Schema(description = "The unique identifier of the test", example = "1")
	private Long testId;

	@Schema(description = "The unique identifier of the course", example = "1")
	private Long courseId;

	@Schema(description = "The title of the course", example = "Chemistry")
	private String courseTitle;

	@Schema(description = "The unique identifier of the teacher who authored the test", example = "1")
	private Long teacherId;

	@Schema(description = "The full name of the teacher", example = "Richard Feynman")
	private String teacherFullName;

	@Schema(description = "The date the test was conducted", example = "2026-05-20")
	private LocalDate date;

	@Schema(description = "The description or title of the test", example = "Mid-term Exam - Chapter 1")
	private String description;

	@Schema(description = "A nested list containing the individual results/grades of the students assigned to this test")
	private List<AdminTestResultResponseResource> results;

}

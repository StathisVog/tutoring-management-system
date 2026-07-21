package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * REST API response representing an individual student's test result from the teacher's
 * perspective.
 */
@Data
@Schema(description = "Represents a student's result for a specific test")
public class TeachersTestResultResource {

	@Schema(description = "The unique identifier of the test result", example = "1")
	private Long testResultId;

	@Schema(description = "The full name of the student", example = "Christos Lymperis")
	private String studentFullName;

	@Schema(description = "The grade the student received. Null if not yet graded.", example = "20.00", nullable = true)
	private BigDecimal grade;

	@Schema(description = "Optional feedback from the teacher", example = "Great effort!", nullable = true)
	private String comments;

}

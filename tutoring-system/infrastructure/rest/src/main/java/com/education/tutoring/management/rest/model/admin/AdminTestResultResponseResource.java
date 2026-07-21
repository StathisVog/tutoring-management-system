package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * REST resource representing an individual student's test result for administrative API
 * responses.
 */
@Data
@Schema(description = "Detailed test result for a specific student, used in administrative views")
public class AdminTestResultResponseResource {

	@Schema(description = "The unique identifier of the test result (the graded paper)", example = "1")
	private Long testResultId;

	@Schema(description = "The unique identifier of the student", example = "1")
	private Long studentId;

	@Schema(description = "The full name of the student", example = "Giannis Dimitriou")
	private String studentFullName;

	@Schema(description = "The final grade achieved by the student. Can be null if not yet graded.", example = "18.50")
	private BigDecimal grade;

	@Schema(description = "Optional comments or feedback provided by the teacher", example = "Excellent progress!")
	private String comments;

}

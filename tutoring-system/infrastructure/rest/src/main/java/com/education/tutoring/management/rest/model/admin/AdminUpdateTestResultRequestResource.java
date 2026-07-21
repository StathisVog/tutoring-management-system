package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * REST request payload used by administrators to modify an individual student's test
 * result.
 */
@Data
@Schema(description = "Payload for updating a specific student's test result (grade and comments)")
public class AdminUpdateTestResultRequestResource {

	@NotNull(message = "Test result ID is required")
	@Schema(description = "The unique identifier of the test result being updated", example = "1")
	private Long testResultId;

	@DecimalMin(value = "0.00", message = "Grade must be at least 0")
	@DecimalMax(value = "20.00", message = "Grade cannot exceed 20")
	@Schema(description = "The new grade for the student. Can be null to clear the grade.", example = "18.50")
	private BigDecimal grade;

	@Schema(description = "The updated comments for the student", example = "Grade adjusted after review.")
	private String comments;

}

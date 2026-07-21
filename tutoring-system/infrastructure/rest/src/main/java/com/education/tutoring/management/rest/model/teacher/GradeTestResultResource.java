package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * REST request payload used by teachers to submit or update a specific student's test
 * grade and feedback.
 */
@Data
@Schema(description = "Payload for updating a student's test grade")
public class GradeTestResultResource {

	@NotNull(message = "Grade cannot be null")
	@DecimalMin(value = "0.00", message = "Grade must be at least 0.00")
	@DecimalMax(value = "20.00", message = "Grade cannot exceed 20.00")
	@Schema(description = "The grade assigned to the student (0.00 to 20.00)", example = "18.50")
	private BigDecimal grade;

	@Schema(description = "Optional feedback or comments from the teacher regarding the test performance",
			example = "Great effort, but review chapter 1.")
	private String comments;

}

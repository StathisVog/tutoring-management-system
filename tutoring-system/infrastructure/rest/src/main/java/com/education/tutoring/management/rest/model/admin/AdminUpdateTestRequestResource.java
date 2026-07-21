package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * REST request payload used by administrators to update a test and its nested student
 * results.
 */
@Data
@Schema(description = "Payload for an Admin to forcefully update a test and optionally override its nested student results")
public class AdminUpdateTestRequestResource {

	@NotNull(message = "Test date is required")
	@Schema(description = "The new date for the test", example = "2026-06-15")
	private LocalDate date;

	@NotBlank(message = "Test description is required")
	@Schema(description = "The updated description or title of the test", example = "Updated: Chemistry Final")
	private String description;

	@Valid
	@Schema(description = "Optional list of student results to update. Only included results will be modified.")
	private List<AdminUpdateTestResultRequestResource> resultsToUpdate = new ArrayList<>();

}

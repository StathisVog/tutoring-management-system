package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST request payload used by administrators to forcefully update an existing lesson
 * activity.
 */
@Data
@Schema(description = "Request payload for an Admin to forcefully update a lesson activity")
public class AdminUpdateLessonActivityRequestResource {

	@NotNull(message = "Date cannot be null")
	@Schema(description = "The new date for the lesson activity", example = "2026-06-15")
	private LocalDate date;

	@NotBlank(message = "Description cannot be blank")
	@Schema(description = "The updated description or syllabus", example = "Updated: Revision on ancient history.")
	private String description;

}

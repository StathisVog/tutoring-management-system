package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST request payload used by administrators to forcefully modify a teacher's absence
 * record.
 */
@Data
@Schema(description = "Payload for an Admin to forcefully update a teacher absence")
public class AdminUpdateTeacherAbsenceRequestResource {

	@NotNull(message = "Date cannot be null")
	@Schema(description = "The updated date of the absence", example = "2026-06-15")
	private LocalDate date;

	@NotBlank(message = "Reason cannot be empty")
	@Schema(description = "The updated reason for the absence", example = "Corrected: Personal Leave")
	private String reason;

	@Schema(description = "The updated slot ID. Set to null to convert to a full-day absence, or provide an ID to convert to a slot-specific absence.",
			example = "1", nullable = true)
	private Long slotId;

}

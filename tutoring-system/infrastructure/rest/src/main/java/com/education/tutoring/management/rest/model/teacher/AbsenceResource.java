package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST request payload used by teachers to declare or modify an upcoming absence.
 */
@Data
@Schema(description = "Payload for a teacher to declare or update an absence")
public class AbsenceResource {

	@NotNull(message = "Date cannot be null")
	@FutureOrPresent(message = "Absence date cannot be in the past")
	@Schema(description = "The date of the absence", example = "2026-05-20")
	private LocalDate date;

	@NotBlank(message = "Reason cannot be empty")
	@Schema(description = "The reason for the absence", example = "Medical appointment")
	private String reason;

	@Schema(description = "The ID of the specific scheduled slot to cancel. "
			+ "If left null, it declares a full-day absence for all slots on that date.", example = "1",
			nullable = true)
	private Long slotId;

}

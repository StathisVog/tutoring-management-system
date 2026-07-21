package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API response representing a teacher's scheduled absence, either for a full day or
 * a specific class slot.
 */
@Data
@Schema(description = "Represents a teacher's declared absence")
public class TeacherAbsenceResponseResource {

	@Schema(description = "The unique identifier of the absence", example = "1")
	private Long id;

	@Schema(description = "The date of the absence", example = "2026-06-01")
	private LocalDate date;

	@Schema(description = "The reason for the absence", example = "Medical appointment")
	private String reason;

	@Schema(description = "The ID of the specific scheduled slot cancelled. If null, indicates a full-day absence.",
			example = "1", nullable = true)
	private Long slotId;

}

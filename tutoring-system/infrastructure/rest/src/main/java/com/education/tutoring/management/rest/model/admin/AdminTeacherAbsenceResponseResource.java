package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST resource representing a teacher's absence (full-day or slot-specific) for
 * administrative API responses.
 */
@Data
@Schema(description = "Detailed view of a teacher's absence, supporting both full-day and slot-specific absences")
public class AdminTeacherAbsenceResponseResource {

	@Schema(description = "The unique identifier of the absence record", example = "1")
	private Long absenceId;

	@Schema(description = "The unique identifier of the teacher", example = "1")
	private Long teacherId;

	@Schema(description = "The full name of the teacher", example = "Richard Feynman")
	private String teacherFullName;

	@Schema(description = "The date of the absence", example = "2026-06-15")
	private LocalDate date;

	@Schema(description = "The reason provided for the absence", example = "Medical leave")
	private String reason;

	@Schema(description = "The ID of the canceled scheduled slot. If null, this is a full-day absence.", example = "1",
			nullable = true)
	private Long slotId;

	@Schema(description = "The title of the course for the canceled slot. Null if full-day absence.",
			example = "Chemistry", nullable = true)
	private String courseTitle;

}

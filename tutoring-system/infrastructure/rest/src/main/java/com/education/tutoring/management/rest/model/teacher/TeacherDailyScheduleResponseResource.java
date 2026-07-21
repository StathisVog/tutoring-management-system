package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * REST API response representing a teacher's daily teaching timetable, grouping multiple
 * class slots for a specific date.
 */
@Data
@Schema(description = "The schedule container for a specific date for a teacher")
public class TeacherDailyScheduleResponseResource {

	@Schema(description = "The specific date of this schedule day", example = "2026-05-18")
	private LocalDate date;

	@Schema(description = "The day of the week", example = "MONDAY")
	private DayOfWeek dayOfWeek;

	@Schema(description = "The list of scheduled slots for this specific day to be taught")
	private List<TeacherScheduleSlotResponseResource> slots;

}

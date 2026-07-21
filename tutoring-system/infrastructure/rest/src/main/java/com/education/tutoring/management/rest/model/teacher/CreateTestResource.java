package com.education.tutoring.management.rest.model.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * REST request payload used by teachers to schedule a new test and assign it to a
 * specific class or subset of students.
 */
@Data
@Schema(description = "Payload for creating and assigning a new test to a class or specific students")
public class CreateTestResource {

	@NotNull(message = "Course ID is required")
	@Schema(description = "The ID of the course this test belongs to", example = "1")
	private Long courseId;

	@NotNull(message = "Scheduled Slot ID is required")
	@Schema(description = "The ID of the scheduled slot (class) to assign this test to", example = "1")
	private Long scheduledSlotId;

	@NotNull(message = "Test date is required")
	@FutureOrPresent(message = "Test date cannot be in the past")
	@Schema(description = "The date the test will take place", example = "2026-07-16")
	private LocalDate date;

	@NotBlank(message = "Test description/title is required")
	@Schema(description = "A description or title for the test",
			example = "Test 1: Chemical Equilibrium & Acid-Base Theories")
	private String description;

	@Schema(description = "Optional list of student IDs. If empty, the test is assigned to the whole class.",
			example = "[1, 2]")
	private List<Long> studentIds = new ArrayList<>();

}

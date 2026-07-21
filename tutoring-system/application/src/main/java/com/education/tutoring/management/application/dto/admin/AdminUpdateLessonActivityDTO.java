package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating an existing lesson activity.
 */
@Data
@Builder
public class AdminUpdateLessonActivityDTO {

	private LocalDate date;

	private String description;

}

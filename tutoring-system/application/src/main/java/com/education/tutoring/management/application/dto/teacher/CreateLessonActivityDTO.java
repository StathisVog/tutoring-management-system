package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new lesson activity.
 */
@Data
@Builder
public class CreateLessonActivityDTO {

	private LocalDate date;

	private String description;

}

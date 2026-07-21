package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a lesson activity and its details.
 */
@Data
@Builder
public class LessonActivityDTO {

	private Long id;

	private Long slotId;

	private String courseTitle;

	private LocalDate date;

	private String description;

}

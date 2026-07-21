package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for updating the description of an existing lesson activity.
 */
@Data
@Builder
public class UpdateLessonActivityDTO {

	private String description;

}

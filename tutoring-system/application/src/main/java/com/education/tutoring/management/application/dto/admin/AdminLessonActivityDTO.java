package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing lesson activity details for admin views.
 */
@Data
@Builder
public class AdminLessonActivityDTO {

	private Long slotId;

	private Long courseId;

	private String courseTitle;

	private Long teacherId;

	private String teacherFullName;

	private Long activityId;

	private LocalDate date;

	private String description;

}

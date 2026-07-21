package com.education.tutoring.management.application.dto.student;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a lesson activity or homework assigned to a student.
 */
@Data
@Builder
public class StudentLessonActivityDTO {

	private Long activityId;

	private Long slotId;

	private String courseTitle;

	private String teacherFullName;

	private LocalDate date;

	private String description;

}

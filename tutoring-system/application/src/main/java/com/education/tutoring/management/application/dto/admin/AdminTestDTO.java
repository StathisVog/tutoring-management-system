package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object representing a scheduled test and its details for admin views.
 */
@Data
@Builder
public class AdminTestDTO {

	private Long testId;

	private Long courseId;

	private String courseTitle;

	private Long slotId;

	private Long teacherId;

	private String teacherFullName;

	private LocalDate date;

	private String description;

	private List<AdminTestResultDTO> results;

}

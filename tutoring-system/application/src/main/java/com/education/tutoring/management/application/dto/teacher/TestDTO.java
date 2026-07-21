package com.education.tutoring.management.application.dto.teacher;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a scheduled test and its details.
 */
@Data
public class TestDTO {

	private Long id;

	private Long courseId;

	private String courseTitle;

	private Long slotId;

	private LocalDate date;

	private String description;

	private int totalStudentsCount;

	private int gradedStudentsCount;

}

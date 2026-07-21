package com.education.tutoring.management.application.dto.student;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing a student's test result and associated details.
 */
@Data
public class StudentTestResponseDTO {

	private Long testResultId;

	private String courseName;

	private String teacherName;

	private LocalDate date;

	private String description;

	private BigDecimal grade;

	private String comments;

	private String status;

}

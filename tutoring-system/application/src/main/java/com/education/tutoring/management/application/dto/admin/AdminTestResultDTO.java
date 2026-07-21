package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a student's test result for admin views.
 */
@Data
@Builder
public class AdminTestResultDTO {

	private Long testResultId;

	private Long studentId;

	private String studentFullName;

	private BigDecimal grade;

	private String comments;

}

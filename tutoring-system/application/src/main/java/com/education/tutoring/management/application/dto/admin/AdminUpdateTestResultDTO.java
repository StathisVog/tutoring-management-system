package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating a specific student's test result.
 */
@Data
@Builder
public class AdminUpdateTestResultDTO {

	private Long testResultId;

	private BigDecimal grade;

	private String comments;

}

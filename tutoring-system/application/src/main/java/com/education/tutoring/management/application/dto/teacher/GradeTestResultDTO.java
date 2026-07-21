package com.education.tutoring.management.application.dto.teacher;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for submitting grades and comments for a student's test result.
 */
@Data
public class GradeTestResultDTO {

	private BigDecimal grade;

	private String comments;

}

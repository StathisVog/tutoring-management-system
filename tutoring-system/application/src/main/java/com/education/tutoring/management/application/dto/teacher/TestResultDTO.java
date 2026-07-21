package com.education.tutoring.management.application.dto.teacher;

import com.education.tutoring.management.application.dto.user.StudentDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a specific test result for a student.
 */
@Data
public class TestResultDTO {

	private Long id;

	private TestDTO test;

	private StudentDTO student;

	private BigDecimal grade;

	private String comments;

}

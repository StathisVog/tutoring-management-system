package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing an enrolled student's details.
 */
@Data
@Builder
public class EnrolledStudentDTO {

	private Long studentId;

	private String fullName;

	private String email;

	private String username;

	private String address;

	private LocalDate enrollmentDate;

	private String status;

}

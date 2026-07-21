package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing an active student's basic information.
 */
@Data
@Builder
public class ActiveStudentDTO {

	private Long id;

	private String fullName;

	private String username;

}

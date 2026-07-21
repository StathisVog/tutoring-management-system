package com.education.tutoring.management.application.dto.teacher;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for requesting a teacher absence for a specific class.
 */
@Data
public class AbsenceRequestDTO {

	private LocalDate date;

	private String reason;

	private Long slotId;

}

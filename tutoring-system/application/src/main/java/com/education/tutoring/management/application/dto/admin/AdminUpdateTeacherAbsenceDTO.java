package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating an existing teacher absence record.
 */
@Data
@Builder
public class AdminUpdateTeacherAbsenceDTO {

	private LocalDate date;

	private String reason;

	private Long slotId;

}

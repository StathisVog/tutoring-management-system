package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a teacher absence record for admin views.
 */
@Data
@Builder
public class AdminTeacherAbsenceDTO {

	private Long absenceId;

	private Long teacherId;

	private String teacherFullName;

	private LocalDate date;

	private String reason;

	private Long slotId;

	private String courseTitle;

}

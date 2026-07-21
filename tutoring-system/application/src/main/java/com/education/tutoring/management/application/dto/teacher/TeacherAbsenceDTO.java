package com.education.tutoring.management.application.dto.teacher;

import com.education.tutoring.management.application.dto.user.TeacherDTO;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a teacher's absence record.
 */
@Data
public class TeacherAbsenceDTO {

	private Long id;

	private TeacherDTO teacher;

	private LocalDate date;

	private String reason;

	private Long slotId;

}

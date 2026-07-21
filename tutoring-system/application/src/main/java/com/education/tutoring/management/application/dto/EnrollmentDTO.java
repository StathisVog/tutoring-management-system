package com.education.tutoring.management.application.dto;

import com.education.tutoring.management.application.dto.user.StudentDTO;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a student's enrollment in a scheduled slot.
 */
@Data
public class EnrollmentDTO {

	private Long id;

	private StudentDTO student;

	private ScheduledSlotDTO scheduledSlot;

	private LocalDate enrollmentDate;

	private EnrollmentStatus status;

}

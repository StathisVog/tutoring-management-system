package com.education.tutoring.management.application.dto;

import com.education.tutoring.management.domain.util.EnrollmentStatus;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing detailed enrollment information, including student
 * and class details.
 */
@Data
public class EnrollmentResponseDTO {

	private Long studentId;

	private String studentFullName;

	private String studentEmail;

	private Long enrollmentId;

	private LocalDate enrollmentDate;

	private EnrollmentStatus status;

	private Long scheduledSlotId;

	private String courseTitle;

	private String teacherName;

	private DayOfWeek dayOfWeek;

	private LocalTime startTime;

	private LocalTime endTime;

	private String classroom;

}

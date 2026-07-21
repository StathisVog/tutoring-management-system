package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * Data Transfer Object representing a specific scheduled class slot for a teacher.
 */
@Data
@Builder
public class TeacherScheduleSlotDTO {

	private Long slotId;

	private String courseTitle;

	private Long courseId;

	private LocalTime startTime;

	private LocalTime endTime;

	private String classroom;

	private String status;

	private String cancelReason;

	private int enrolledStudentsCount;

}

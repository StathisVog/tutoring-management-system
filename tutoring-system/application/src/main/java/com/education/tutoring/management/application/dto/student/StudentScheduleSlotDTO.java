package com.education.tutoring.management.application.dto.student;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * Data Transfer Object representing a specific scheduled class slot for a student.
 */
@Data
@Builder
public class StudentScheduleSlotDTO {

	private Long slotId;

	private String courseTitle;

	private String teacherName;

	private LocalTime startTime;

	private LocalTime endTime;

	private String classroom;

	private String status;

	private String cancelReason;

	private Long activityId;

	private String activityDescription;

}

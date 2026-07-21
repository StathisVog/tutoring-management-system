package com.education.tutoring.management.application.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Data Transfer Object representing a scheduled class slot, including course, teacher,
 * and capacity details.
 */
@Data
public class ScheduledSlotDTO {

	private Long id;

	private Long courseId;

	private String courseTitle;

	private Long teacherId;

	private String teacherName;

	private DayOfWeek dayOfWeek;

	private LocalTime startTime;

	private LocalTime endTime;

	private String classroom;

	private int capacity;

	private int availableSeats;

}

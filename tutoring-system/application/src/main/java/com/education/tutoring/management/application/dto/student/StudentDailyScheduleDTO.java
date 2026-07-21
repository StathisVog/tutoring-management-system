package com.education.tutoring.management.application.dto.student;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object representing a student's daily schedule and its classes.
 */
@Data
@Builder
public class StudentDailyScheduleDTO {

	private LocalDate date;

	private DayOfWeek dayOfWeek;

	private List<StudentScheduleSlotDTO> slots;

}

package com.education.tutoring.management.application.dto.teacher;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object representing a teacher's daily schedule and its classes.
 */
@Data
@Builder
public class TeacherDailyScheduleDTO {

	private LocalDate date;

	private DayOfWeek dayOfWeek;

	private List<TeacherScheduleSlotDTO> slots;

}

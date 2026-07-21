package com.education.tutoring.management.application.dto.teacher;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating and assigning a new test.
 */
@Data
public class CreateTestRequestDTO {

	private Long courseId;

	private Long scheduledSlotId;

	private LocalDate date;

	private String description;

	private List<Long> studentIds = new ArrayList<>();

}

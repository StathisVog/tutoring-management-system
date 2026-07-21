package com.education.tutoring.management.application.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for updating a scheduled test and its associated results.
 */
@Data
@Builder
public class AdminUpdateTestDTO {

	private LocalDate date;

	private String description;

	private List<AdminUpdateTestResultDTO> resultsToUpdate;

}

package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminTestDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving a dynamically filtered list of tests and their associated
 * results. Designed specifically for administrative oversight and academic monitoring.
 */
public interface GetAdminTests {

	/**
	 * Executes the use case for pulling system-wide tests based on provided optional
	 * filters.
	 * @param teacherId optional filter to find tests authored by a specific teacher
	 * @param courseId optional filter to find tests for a specific course
	 * @param startDate optional lower bound for the test date
	 * @param endDate optional upper bound for the test date
	 * @return a chronologically ordered list of {@link AdminTestDTO} matching the
	 * criteria
	 */
	List<AdminTestDTO> execute(Long teacherId, Long courseId, LocalDate startDate, LocalDate endDate);

}

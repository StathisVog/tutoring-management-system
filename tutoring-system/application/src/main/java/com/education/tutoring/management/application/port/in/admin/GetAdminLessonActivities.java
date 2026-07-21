package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminLessonActivityDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving a dynamically filtered list of lesson activities. Designed
 * specifically for administrative oversight and reporting.
 */
public interface GetAdminLessonActivities {

	/**
	 * Executes the case for pulling system-wide lesson activities based on provided
	 * optional filters.
	 * @param teacherId optional filter to find activities logged by a specific teacher
	 * @param courseId optional filter to find activities for a specific course subject
	 * @param slotId optional filter for a specific class slot
	 * @param startDate optional lower bound for the activity date
	 * @param endDate optional upper bound for the activity date
	 * @return a chronologically ordered list of {@link AdminLessonActivityDTO} matching
	 * the criteria
	 */
	List<AdminLessonActivityDTO> execute(Long teacherId, Long courseId, Long slotId, LocalDate startDate,
			LocalDate endDate);

}

package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.AdminTeacherAbsenceDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for retrieving a dynamically filtered list of teacher absences. Designed to
 * provide administrative oversight of staff availability.
 */
public interface GetAdminTeacherAbsences {

	/**
	 * Executes the case for pulling system-wide teacher absences based on optional
	 * filters.
	 * @param teacherId optional filter to find absences for a specific teacher
	 * @param slotId optional filter to find absences affecting a specific slot
	 * @param startDate optional lower bound date
	 * @param endDate optional upper bound date
	 * @return a chronologically ordered list of {@link AdminTeacherAbsenceDTO}
	 */
	List<AdminTeacherAbsenceDTO> execute(Long teacherId, Long slotId, LocalDate startDate, LocalDate endDate);

}

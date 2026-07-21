package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminTeacherAbsenceDTO;
import com.education.tutoring.management.application.port.in.admin.GetAdminTeacherAbsences;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving teacher absence records for administrative
 * views, utilizing dynamic filtering.
 */
@Slf4j
@UseCase
class GetAdminTeacherAbsencesUseCase implements GetAdminTeacherAbsences {

	private final AdminPersistence adminPersistence;

	public GetAdminTeacherAbsencesUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of teacher absences based on optional filtering criteria.
	 * @param teacherId the optional ID of the teacher to filter by
	 * @param slotId the optional ID of the scheduled slot to filter by
	 * @param startDate the optional start date of the date range filter
	 * @param endDate the optional end date of the date range filter
	 * @return a list of {@link AdminTeacherAbsenceDTO} matching the provided criteria
	 */
	@Override
	public List<AdminTeacherAbsenceDTO> execute(Long teacherId, Long slotId, LocalDate startDate, LocalDate endDate) {

		log.info(
				"Admin is requesting teacher absences with dynamic filters -> TeacherId: {}, SlotId: {}, StartDate: {}, EndDate: {}",
				teacherId != null ? teacherId : "ANY", slotId != null ? slotId : "ANY",
				startDate != null ? startDate : "ANY", endDate != null ? endDate : "ANY");

		return adminPersistence.getTeacherAbsences(teacherId, slotId, startDate, endDate);
	}

}

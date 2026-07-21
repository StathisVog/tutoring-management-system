package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateTeacherAbsenceDTO;
import com.education.tutoring.management.application.port.in.admin.UpdateAdminTeacherAbsence;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively updating a teacher's absence record.
 */
@Slf4j
@UseCase
class UpdateAdminTeacherAbsenceUseCase implements UpdateAdminTeacherAbsence {

	private final AdminPersistence adminPersistence;

	public UpdateAdminTeacherAbsenceUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the forceful update of a teacher absence record by an administrator.
	 * @param absenceId the unique identifier of the absence record to update
	 * @param updateDTO the new data for the absence
	 * @throws ResourceNotFoundException if the absence record cannot be found
	 * @throws IllegalOperationException if the update violates business rules (e.g.,
	 * overlapping schedules)
	 */
	@Override
	public void execute(Long absenceId, AdminUpdateTeacherAbsenceDTO updateDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		log.info("Admin is forcefully updating teacher absence ID: {} (New Date: {}, New SlotId: {})", absenceId,
				updateDTO.getDate(), updateDTO.getSlotId() != null ? updateDTO.getSlotId() : "FULL-DAY");

		adminPersistence.updateTeacherAbsence(absenceId, updateDTO);
	}

}

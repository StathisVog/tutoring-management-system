package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.DeleteAdminTeacherAbsence;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a teacher absence record.
 */
@Slf4j
@UseCase
class DeleteAdminTeacherAbsenceUseCase implements DeleteAdminTeacherAbsence {

	private final AdminPersistence adminPersistence;

	public DeleteAdminTeacherAbsenceUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the use case for permanently deleting a teacher absence record.
	 * @param absenceId the unique identifier of the teacher absence to delete
	 * @throws ResourceNotFoundException if the absence record cannot be found in the
	 * system
	 */
	@Override
	public void execute(Long absenceId) throws ResourceNotFoundException {

		log.warn("Admin is permanently deleting teacher absence ID: {}.", absenceId);

		adminPersistence.deleteTeacherAbsence(absenceId);
	}

}

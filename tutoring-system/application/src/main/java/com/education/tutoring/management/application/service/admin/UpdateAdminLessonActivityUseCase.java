package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminUpdateLessonActivityDTO;
import com.education.tutoring.management.application.port.in.admin.UpdateAdminLessonActivity;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively updating an existing lesson activity.
 */
@Slf4j
@UseCase
class UpdateAdminLessonActivityUseCase implements UpdateAdminLessonActivity {

	private final AdminPersistence adminPersistence;

	public UpdateAdminLessonActivityUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the forceful update of a lesson activity by an administrator.
	 * @param activityId the unique identifier of the activity to update
	 * @param adminUpdateLessonActivityDTO the new data for the lesson activity
	 * @throws ResourceNotFoundException if the activity cannot be found in the system
	 * @throws IllegalOperationException if the update violates business rules
	 */
	@Override
	public void execute(Long activityId, AdminUpdateLessonActivityDTO adminUpdateLessonActivityDTO)
			throws ResourceNotFoundException, IllegalOperationException {

		log.info("Admin is forcefully updating lesson activity ID: {} to new date: {}", activityId,
				adminUpdateLessonActivityDTO.getDate());

		adminPersistence.updateLessonActivity(activityId, adminUpdateLessonActivityDTO);
	}

}

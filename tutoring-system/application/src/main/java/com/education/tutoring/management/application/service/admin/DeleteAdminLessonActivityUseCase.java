package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.port.in.admin.DeleteAdminLessonActivity;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case implementation for administratively deleting a lesson activity.
 */
@Slf4j
@UseCase
class DeleteAdminLessonActivityUseCase implements DeleteAdminLessonActivity {

	private final AdminPersistence adminPersistence;

	public DeleteAdminLessonActivityUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the use case for permanently deleting a lesson activity.
	 * @param activityId the ID of the activity to delete
	 * @throws ResourceNotFoundException if the activity cannot be found in the system
	 */
	@Override
	public void execute(Long activityId) throws ResourceNotFoundException {

		log.warn("Admin is permanently deleting lesson activity ID: {}", activityId);

		adminPersistence.deleteLessonActivity(activityId);
	}

}

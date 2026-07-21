package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminLessonActivityDTO;
import com.education.tutoring.management.application.port.in.admin.GetAdminLessonActivities;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving lesson activities for administrative views,
 * utilizing dynamic filtering.
 */
@Slf4j
@UseCase
class GetAdminLessonActivitiesUseCase implements GetAdminLessonActivities {

	private final AdminPersistence adminPersistence;

	public GetAdminLessonActivitiesUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of lesson activities based on optional filtering criteria.
	 * @param teacherId the optional ID of the teacher to filter by
	 * @param courseId the optional ID of the course to filter by
	 * @param slotId the optional ID of the scheduled slot to filter by
	 * @param startDate the optional start date of the date range filter
	 * @param endDate the optional end date of the date range filter
	 * @return a list of {@link AdminLessonActivityDTO} matching the provided criteria
	 */
	@Override
	public List<AdminLessonActivityDTO> execute(Long teacherId, Long courseId, Long slotId, LocalDate startDate,
			LocalDate endDate) {

		log.info(
				"Admin is requesting lesson activities with dynamic filters -> TeacherId: {}, CourseId: {}, SlotId: {}, StartDate: {}, EndDate: {}",
				teacherId != null ? teacherId : "ANY", courseId != null ? courseId : "ANY",
				slotId != null ? slotId : "ANY", startDate != null ? startDate : "ANY",
				endDate != null ? endDate : "ANY");

		return adminPersistence.getLessonActivities(teacherId, courseId, slotId, startDate, endDate);
	}

}

package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.AdminTestDTO;
import com.education.tutoring.management.application.port.in.admin.GetAdminTests;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving scheduled tests for administrative views,
 * utilizing dynamic filtering.
 */
@Slf4j
@UseCase
class GetAdminTestsUseCase implements GetAdminTests {

	private final AdminPersistence adminPersistence;

	public GetAdminTestsUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of scheduled tests based on optional filtering criteria.
	 * @param teacherId the optional ID of the teacher to filter by
	 * @param courseId the optional ID of the course to filter by
	 * @param startDate the optional start date of the date range filter
	 * @param endDate the optional end date of the date range filter
	 * @return a list of {@link AdminTestDTO} matching the provided criteria
	 */
	@Override
	public List<AdminTestDTO> execute(Long teacherId, Long courseId, LocalDate startDate, LocalDate endDate) {

		log.info(
				"Admin is requesting tests with dynamic filters -> TeacherId: {}, CourseId: {}, StartDate: {}, EndDate: {}",
				teacherId != null ? teacherId : "ANY", courseId != null ? courseId : "ANY",
				startDate != null ? startDate : "ANY", endDate != null ? endDate : "ANY");

		return adminPersistence.getTests(teacherId, courseId, startDate, endDate);
	}

}

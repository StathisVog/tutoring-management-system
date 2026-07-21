package com.education.tutoring.management.application.service.admin;

import com.education.tutoring.management.application.dto.admin.EnrolledStudentDTO;
import com.education.tutoring.management.application.port.in.admin.GetEnrolledStudentsBySlot;
import com.education.tutoring.management.application.port.out.AdminPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving the roster of active students enrolled in a
 * specific scheduled slot.
 */
@Slf4j
@UseCase
class GetEnrolledStudentsBySlotUseCase implements GetEnrolledStudentsBySlot {

	private final AdminPersistence adminPersistence;

	public GetEnrolledStudentsBySlotUseCase(AdminPersistence adminPersistence) {
		this.adminPersistence = adminPersistence;
	}

	/**
	 * Executes the retrieval of enrolled students for a given class slot.
	 * @param scheduledSlotId the unique identifier of the scheduled slot
	 * @return a list of {@link EnrolledStudentDTO} representing the class roster
	 * @throws ResourceNotFoundException if the scheduled slot cannot be found
	 */
	@Override
	public List<EnrolledStudentDTO> execute(Long scheduledSlotId) throws ResourceNotFoundException {

		log.debug("Admin requesting class roster for scheduled slot ID: {}", scheduledSlotId);
		return adminPersistence.getEnrolledStudentsBySlot(scheduledSlotId);
	}

}

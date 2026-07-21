package com.education.tutoring.management.application.port.in.admin;

import com.education.tutoring.management.application.dto.admin.EnrolledStudentDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for retrieving the roster of students enrolled in a specific class.
 */
public interface GetEnrolledStudentsBySlot {

	/**
	 * Executes the retrieval of the class roster.
	 * @param scheduledSlotId the ID of the scheduled slot to query
	 * @return a list of {@link EnrolledStudentDTO} containing student details
	 * @throws ResourceNotFoundException if the requested slot does not exist
	 */
	List<EnrolledStudentDTO> execute(Long scheduledSlotId) throws ResourceNotFoundException;

}

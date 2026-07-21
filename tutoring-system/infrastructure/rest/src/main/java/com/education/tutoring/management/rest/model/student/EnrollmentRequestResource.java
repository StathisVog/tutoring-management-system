package com.education.tutoring.management.rest.model.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * REST request payload used by a student to initiate enrollment in a specific scheduled
 * class slot.
 */
@Data
@Schema(description = "Payload for a student to request enrollment in a specific class slot")
public class EnrollmentRequestResource {

	@NotNull(message = "Scheduled Slot ID is required")
	@Schema(description = "The ID of the scheduled slot to enroll in", example = "1")
	private Long slotId;

}

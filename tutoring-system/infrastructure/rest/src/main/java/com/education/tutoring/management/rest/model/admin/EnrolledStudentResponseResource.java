package com.education.tutoring.management.rest.model.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * REST API response representing a student enrolled in a specific scheduled class slot.
 */
@Data
@Schema(description = "Details of a student enrolled in a specific scheduled class (slot)")
public class EnrolledStudentResponseResource {

	@Schema(description = "The unique identifier of the student", example = "1")
	private Long studentId;

	@Schema(description = "The full name of the student", example = "George Papadopoulos")
	private String fullName;

	@Schema(description = "The contact email address of the student", example = "george@example.com")
	private String email;

	@Schema(description = "The system username of the student", example = "georgepap2026")
	private String username;

	@Schema(description = "The home address of the student", example = "Volos, Greece", nullable = true)
	private String address;

	@Schema(description = "The date the student was enrolled in the class", example = "2026-05-12")
	private LocalDate enrollmentDate;

	@Schema(description = "The current status of the enrollment (e.g., ACTIVE, PENDING_DROP)", example = "ACTIVE")
	private String status;

}

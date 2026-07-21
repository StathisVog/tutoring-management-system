package com.education.tutoring.management.application.dto.user;

import com.education.tutoring.management.domain.util.SchoolClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object representing a student, extending the base UserDTO.
 */
@Data
// callSuper = true => It also includes the fields of the Parent Class (UserDTO)
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends UserDTO {

	private int age;

	private SchoolClass schoolClass;

	private String parentFullName;

	private String parentTaxId;

}

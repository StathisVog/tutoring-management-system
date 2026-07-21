package com.education.tutoring.management.rest.model.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * REST API response representing a direct assignment link between a course and a teacher.
 */
@Data
@Schema(description = "Details of a specific teacher-to-course assignment record")
public class CourseAssignmentResource {

	@Schema(description = "The ID of the course", example = "1")
	private Long courseId;

	@Schema(description = "The title of the course", example = "Chemistry - High School 3rd Year (Advanced)")
	private String courseTitle;

	@Schema(description = "The ID of the teacher assigned to the course", example = "1")
	private Long teacherId;

	@Schema(description = "The full name of the teacher", example = "Dimitris Ioannidis")
	private String teacherName;

	@Schema(description = "The specialty of the teacher", example = "Physicist / Chemist")
	private String specialty;

}

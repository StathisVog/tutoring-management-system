package com.education.tutoring.management.application.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the assignment of a teacher to a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignmentDTO {

	private Long courseId;

	private String courseTitle;

	private Long teacherId;

	private String teacherName;

	private String specialty;

}

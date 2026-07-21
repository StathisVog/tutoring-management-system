package com.education.tutoring.management.application.dto.course;

import com.education.tutoring.management.application.dto.user.TeacherDTO;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object representing a course and its details, including assigned
 * teachers.
 */
@Data
public class CourseDTO {

	private Long id;

	private String title;

	private String description;

	private String gradeLevel;

	private boolean active;

	private List<TeacherDTO> teachers;

}

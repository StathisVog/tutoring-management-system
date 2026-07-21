package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JPA Entity representing an educational course. Acts as the central aggregate for
 * assigned teachers, scheduled class slots, and tests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "teachers", "scheduledSlots", "tests" })
@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "grade_level")
	private String gradeLevel;

	@Column(name = "active")
	private boolean active;

	@Builder.Default
	@ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
	private Set<Teacher> teachers = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ScheduledSlot> scheduledSlots = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Test> tests = new ArrayList<>();

	// Helper (Synchronizing) Methods

	public void addTeacher(Teacher teacher) {
		if (teachers.add(teacher)) {
			teacher.getCourses().add(this);
		}
	}

	public void removeTeacher(Teacher teacher) {
		if (teachers.remove(teacher)) {
			teacher.getCourses().remove(this);
		}
	}

	public void addScheduledSlot(ScheduledSlot scheduledSlot) {
		if (!scheduledSlots.contains(scheduledSlot)) {
			scheduledSlots.add(scheduledSlot);
			scheduledSlot.setCourse(this);
		}
	}

	public void removeScheduledSlot(ScheduledSlot scheduledSlot) {
		if (scheduledSlots.remove(scheduledSlot)) {
			scheduledSlot.setCourse(null);
		}
	}

	public void addTest(Test test) {
		if (!tests.contains(test)) {
			tests.add(test);
			test.setCourse(this);
		}
	}

	public void removeTest(Test test) {
		if (tests.remove(test)) {
			test.setCourse(null);
		}
	}

}

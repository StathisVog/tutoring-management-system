package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JPA Entity representing a teacher user in the system. Extends the base User entity and
 * manages the teacher's course assignments, schedule, and authored tests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = { "courses", "scheduledSlots", "teacherAbsences", "tests" })
@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "id")
public class Teacher extends User {

	@Column(name = "specialty", length = 100)
	private String specialty;

	@Column(name = "bio", columnDefinition = "TEXT")
	private String bio;

	// The intermediate table is for assignments
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "teacher_course", joinColumns = @JoinColumn(name = "teacher_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> courses = new HashSet<>();

	// The intermediate table is for teachers' preferences during registration
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "teacher_eligible_course", joinColumns = @JoinColumn(name = "teacher_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> eligibleCourses = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ScheduledSlot> scheduledSlots = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TeacherAbsence> teacherAbsences = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Test> tests = new ArrayList<>();

	// Helper (Synchronizing) Methods

	public void addCourse(Course course) {
		if (courses.add(course)) {
			course.getTeachers().add(this);
		}
	}

	public void removeCourse(Course course) {
		if (courses.remove(course)) {
			course.getTeachers().remove(this);
		}
	}

	public void addScheduledSlot(ScheduledSlot scheduledSlot) {
		if (!scheduledSlots.contains(scheduledSlot)) {
			scheduledSlots.add(scheduledSlot);
			scheduledSlot.setTeacher(this);
		}
	}

	public void removeScheduledSlot(ScheduledSlot scheduledSlot) {
		if (scheduledSlots.remove(scheduledSlot)) {
			scheduledSlot.setTeacher(null);
		}
	}

	public void addTeacherAbsence(TeacherAbsence teacherAbsence) {
		if (!teacherAbsences.contains(teacherAbsence)) {
			teacherAbsences.add(teacherAbsence);
			teacherAbsence.setTeacher(this);
		}
	}

	public void removeTeacherAbsence(TeacherAbsence teacherAbsence) {
		if (teacherAbsences.remove(teacherAbsence)) {
			teacherAbsence.setTeacher(null);
		}
	}

	public void addTest(Test test) {
		if (!tests.contains(test)) {
			tests.add(test);
			test.setAuthor(this);
		}
	}

	public void removeTest(Test test) {
		if (tests.remove(test)) {
			test.setAuthor(null);
		}
	}

	// the preference only concerns the teacher's profile
	public void addEligibleCourse(Course course) {
		eligibleCourses.add(course);
	}

}

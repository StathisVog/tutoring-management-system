package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a specific scheduled timetable slot for a course. Binds a
 * specific teacher to a course on a recurring day and time, and manages student
 * enrollments.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "course", "teacher", "enrollments", "lessonActivities" })
@Entity
@Table(name = "scheduled_slots",
		uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "teacher_id", "day_of_week", "start_time" }))
public class ScheduledSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false, length = 10)
	private DayOfWeek dayOfWeek;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(name = "classroom", length = 50)
	private String classroom;

	@Min(1)
	@Column(name = "capacity", nullable = false)
	private int capacity;

	@Builder.Default
	@OneToMany(mappedBy = "scheduledSlot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Enrollment> enrollments = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "scheduledSlot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LessonActivity> lessonActivities = new ArrayList<>();

	// Helper (Synchronizing) Methods

	public void addEnrollment(Enrollment enrollment) {
		if (!enrollments.contains(enrollment)) {
			enrollments.add(enrollment);
			enrollment.setScheduledSlot(this);
		}
	}

	public void removeEnrollment(Enrollment enrollment) {
		if (enrollments.remove(enrollment)) {
			enrollment.setScheduledSlot(null);
		}
	}

	public void addLessonActivity(LessonActivity lessonActivity) {
		if (!lessonActivities.contains(lessonActivity)) {
			lessonActivities.add(lessonActivity);
			lessonActivity.setScheduledSlot(this);
		}
	}

	public void removeLessonActivity(LessonActivity lessonActivity) {
		if (lessonActivities.remove(lessonActivity)) {
			lessonActivity.setScheduledSlot(null);
		}
	}

}

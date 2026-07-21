package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing an examination or test scheduled for a specific course and
 * class slot. Authored by a teacher and acts as the parent aggregate for individual
 * student test results.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "course", "testResults", "scheduledSlot" })
@Entity
@Table(name = "tests", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "date" }))
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduled_slot_id", nullable = false)
	private ScheduledSlot scheduledSlot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher author;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "description")
	private String description;

	@Builder.Default
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TestResult> testResults = new ArrayList<>();

	// Helper (Synchronizing) Methods

	public void addTestResult(TestResult testResult) {
		if (testResult.getId() == null || !testResults.contains(testResult)) {
			testResults.add(testResult);
			testResult.setTest(this);
		}
	}

	public void removeTestResult(TestResult testResult) {
		if (testResults.remove(testResult)) {
			testResult.setTest(null);
		}
	}

}

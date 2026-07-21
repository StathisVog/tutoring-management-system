package com.education.tutoring.management.repository.jpa.entity;

import com.education.tutoring.management.domain.util.SchoolClass;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a student user in the system. Extends the base User entity to
 * include student-specific attributes such as grade level and parent information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = { "enrollments", "testResults" })
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {

	@Min(5)
	@Max(100)
	@Column(name = "age", nullable = false)
	private int age;

	@Enumerated(EnumType.STRING)
	@Column(name = "school_class", nullable = false, length = 20)
	private SchoolClass schoolClass;

	@Column(name = "parent_full_name", length = 100)
	private String parentFullName;

	@Column(name = "parent_tax_id", length = 20, unique = true)
	private String parentTaxId;

	@Builder.Default
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Enrollment> enrollments = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TestResult> testResults = new ArrayList<>();

	// Helper (Synchronizing) Methods

	public void addEnrollment(Enrollment enrollment) {
		if (!enrollments.contains(enrollment)) {
			enrollments.add(enrollment);
			enrollment.setStudent(this);
		}
	}

	public void removeEnrollment(Enrollment enrollment) {
		if (enrollments.remove(enrollment)) {
			enrollment.setStudent(null);
		}
	}

	public void addTestResult(TestResult testResult) {
		if (!testResults.contains(testResult)) {
			testResults.add(testResult);
			testResult.setStudent(this);
		}
	}

	public void removeTestResult(TestResult testResult) {
		if (testResults.remove(testResult)) {
			testResult.setStudent(null);
		}
	}

}

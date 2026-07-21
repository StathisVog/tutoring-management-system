package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

/**
 * JPA Entity representing a student's graded outcome for a specific scheduled test.
 * Connects a student to a test instance and stores their exact score and teacher
 * feedback.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "test", "student" })
@Entity
@Table(name = "test_results", uniqueConstraints = @UniqueConstraint(columnNames = { "test_id", "student_id" }))
public class TestResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@DecimalMin("0.00")
	@DecimalMax("20.00")
	@Column(name = "grade", precision = 4, scale = 2)
	private BigDecimal grade;

	@Column(name = "comments", length = 500)
	private String comments;

}

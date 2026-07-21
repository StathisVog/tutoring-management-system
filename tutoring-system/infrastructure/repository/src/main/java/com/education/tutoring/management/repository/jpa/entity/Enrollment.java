package com.education.tutoring.management.repository.jpa.entity;

import com.education.tutoring.management.domain.util.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA Entity representing a student's enrollment in a specific scheduled class slot.
 * Tracks the ongoing lifecycle of the enrollment via its status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "student", "scheduledSlot" })
@Entity
@Table(name = "enrollments")
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduled_slot_id", nullable = false)
	private ScheduledSlot scheduledSlot;

	@Column(name = "enrollment_date", nullable = false)
	private LocalDate enrollmentDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private EnrollmentStatus status;

}

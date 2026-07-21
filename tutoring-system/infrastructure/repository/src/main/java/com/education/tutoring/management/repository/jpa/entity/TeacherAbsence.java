package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA Entity representing a logged absence for a specific teacher. Can be associated with
 * a particular scheduled class slot or indicate a general absence for the given date.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "teacher")
@Entity
@Table(name = "teacher_absences",
		uniqueConstraints = @UniqueConstraint(columnNames = { "teacher_id", "date", "scheduled_slot_id" }))
public class TeacherAbsence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "reason")
	private String reason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduled_slot_id")
	private ScheduledSlot scheduledSlot;

}

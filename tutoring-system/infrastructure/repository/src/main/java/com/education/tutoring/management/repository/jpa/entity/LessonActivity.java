package com.education.tutoring.management.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA Entity representing a specific lesson log or activity recorded by a teacher for a
 * scheduled class slot on a particular date.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "scheduledSlot" })
@Entity
@Table(name = "lesson_activities",
		indexes = { @Index(name = "idx_lesson_activity_slot", columnList = "scheduled_slot_id") })
public class LessonActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduled_slot_id", nullable = false)
	private ScheduledSlot scheduledSlot;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

}

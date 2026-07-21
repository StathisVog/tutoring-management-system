package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.domain.util.EnrollmentStatus;
import com.education.tutoring.management.repository.jpa.entity.ScheduledSlot;
import com.education.tutoring.management.repository.jpa.mapper.EntityMapper;
import com.education.tutoring.management.repository.jpa.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Specialized utility service responsible for enriching {@link ScheduledSlotDTO} objects.
 * Bridges the entity mapping process with real-time capacity calculations by querying the
 * persistence layer for active and pending enrollments, ensuring accurate seat
 * availability reporting across the system.
 */
@Service
@RequiredArgsConstructor
public class ScheduledSlotAvailabilityCalculator {

	private final EnrollmentRepository enrollmentRepository;

	private static final EntityMapper mapper = EntityMapper.MAPPER;

	/**
	 * Maps a ScheduledSlot entity to DTO and enriches it with the dynamic calculation of
	 * available seats based on active enrollments.
	 */
	public ScheduledSlotDTO toEnrichedDto(ScheduledSlot scheduledSlot) {

		ScheduledSlotDTO scheduledSlotDTO = mapper.toScheduledSlotDTO(scheduledSlot);

		int occupiedSeats = enrollmentRepository.countByScheduledSlotIdAndStatusIn(scheduledSlot.getId(),
				List.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.PENDING_DROP, EnrollmentStatus.PENDING_ENROLL));

		int availableSeats = scheduledSlot.getCapacity() - occupiedSeats;
		scheduledSlotDTO.setAvailableSeats(availableSeats);

		return scheduledSlotDTO;
	}

}

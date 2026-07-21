package com.education.tutoring.management.repository.jpa.service;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.out.ScheduledSlotPersistence;
import com.education.tutoring.management.repository.jpa.repository.ScheduledSlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA-based implementation of the {@link ScheduledSlotPersistence} output port. Acts as
 * the persistence adapter responsible for retrieving system-wide course timetables.
 * Orchestrates the fetching of scheduled class slots and collaborates with enrichment
 * utilities to provide dynamically calculated, real-time seat availability to the
 * application layer.
 */
@Service
@AllArgsConstructor
public class ScheduledSlotPersistenceImpl implements ScheduledSlotPersistence {

	private final ScheduledSlotRepository scheduledSlotRepository;

	private final ScheduledSlotAvailabilityCalculator scheduledSlotAvailabilityCalculator;

	/**
	 * Retrieves all scheduled slots and maps them to DTOs.
	 * @return a list of mapped {@link ScheduledSlotDTO}s
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ScheduledSlotDTO> getAllScheduledSlots() {

		return scheduledSlotRepository.findAllByOrderByDayOfWeekAscStartTimeAsc()
			.stream()
			.map(scheduledSlotAvailabilityCalculator::toEnrichedDto)
			.toList();
	}

}

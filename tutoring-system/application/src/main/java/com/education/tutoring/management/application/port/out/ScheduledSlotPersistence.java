package com.education.tutoring.management.application.port.out;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;

import java.util.List;

/**
 * Interface for scheduled slot persistence operations.
 */
public interface ScheduledSlotPersistence {

	/**
	 * Retrieves all scheduled slots across all courses in the system, ordered
	 * chronologically by day and time.
	 * @return a list of {@link ScheduledSlotDTO} representing the entire schedule
	 */
	List<ScheduledSlotDTO> getAllScheduledSlots();

}

package com.education.tutoring.management.application.port.in.scheduledSlot;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;

import java.util.List;

/**
 * Interface for retrieving the complete system timetable across all courses and slots.
 */
public interface GetAllScheduledSlots {

	/**
	 * Executes the retrieval of the complete system timetable.
	 * @return a list of {@link ScheduledSlotDTO} representing all scheduled slots
	 */
	List<ScheduledSlotDTO> execute();

}

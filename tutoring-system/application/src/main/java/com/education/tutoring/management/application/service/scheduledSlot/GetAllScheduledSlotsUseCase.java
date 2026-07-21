package com.education.tutoring.management.application.service.scheduledSlot;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.in.scheduledSlot.GetAllScheduledSlots;
import com.education.tutoring.management.application.port.out.ScheduledSlotPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Use case implementation for retrieving the complete system timetable of scheduled
 * slots.
 */
@Slf4j
@UseCase
class GetAllScheduledSlotsUseCase implements GetAllScheduledSlots {

	private final ScheduledSlotPersistence scheduledSlotPersistence;

	public GetAllScheduledSlotsUseCase(ScheduledSlotPersistence scheduledSlotPersistence) {
		this.scheduledSlotPersistence = scheduledSlotPersistence;
	}

	/**
	 * Executes the retrieval of all scheduled slots across the system.
	 * @return a list of {@link ScheduledSlotDTO} representing the global timetable
	 */
	@Override
	public List<ScheduledSlotDTO> execute() {

		log.debug("Fetching all scheduled slots across the system.");
		return scheduledSlotPersistence.getAllScheduledSlots();
	}

}

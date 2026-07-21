package com.education.tutoring.management.rest.controller;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.application.port.in.scheduledSlot.GetAllScheduledSlots;
import com.education.tutoring.management.rest.adapter.ScheduledSlotResourceMapper;
import com.education.tutoring.management.rest.model.ScheduledSlotResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller acting as the primary driving adapter for global timetable management.
 * Exposes endpoints to aggregate and retrieve scheduled class slots across the entire
 * system, decoupling the schedule retrieval process from specific course hierarchies.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slots")
@Tag(name = "Scheduled Slot API", description = "Endpoints for managing the global timetable")
public class ScheduledSlotController {

	private final GetAllScheduledSlots getAllScheduledSlots;

	/**
	 * Retrieves the comprehensive, system-wide timetable. Executes the underlying use
	 * case to fetch all scheduled slots—enriched with real-time seat availability— and
	 * maps the resulting data to appropriate API resources.
	 * @return a {@link ResponseEntity} containing a list of {@link ScheduledSlotResource}
	 * representing the global schedule
	 */
	@Operation(summary = "Get all scheduled slots",
			description = "Retrieves the complete timetable for the entire system.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of all scheduled slots")
	@ApiResponse(responseCode = "403", description = "Forbidden - User does not have Admin privileges")
	@GetMapping
	ResponseEntity<List<ScheduledSlotResource>> getAllSlots() {

		List<ScheduledSlotDTO> slotDTOs = getAllScheduledSlots.execute();

		List<ScheduledSlotResource> resources = ScheduledSlotResourceMapper.MAPPER.toResourceList(slotDTOs);

		return ResponseEntity.ok(resources);
	}

}

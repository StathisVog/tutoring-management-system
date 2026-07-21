package com.education.tutoring.management.rest.adapter;

import com.education.tutoring.management.application.dto.ScheduledSlotDTO;
import com.education.tutoring.management.rest.model.course.CreateScheduledSlotResource;
import com.education.tutoring.management.rest.model.ScheduledSlotResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for scheduled class slot data. Translates inbound REST payloads for
 * creating timetables into application DTOs, and formats internal slot data for API
 * responses.
 */
@Mapper
public interface ScheduledSlotResourceMapper {

	/** Singleton instance of the mapper. */
	ScheduledSlotResourceMapper MAPPER = Mappers.getMapper(ScheduledSlotResourceMapper.class);

	/**
	 * Maps a CreateScheduledSlotResource to a ScheduledSlotDTO
	 * @param createScheduledSlotResource the incoming creation payload
	 * @return mapped ScheduledSlotDTO
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "courseId", ignore = true)
	@Mapping(target = "courseTitle", ignore = true)
	@Mapping(target = "teacherName", ignore = true)
	@Mapping(target = "availableSeats", ignore = true)
	ScheduledSlotDTO toSlotDTO(CreateScheduledSlotResource createScheduledSlotResource);

	/**
	 * Maps a ScheduledSlotDTO to a ScheduledSlotResource.
	 * @param scheduledSlotDTO the source DTO
	 * @return mapped ScheduledSlotResource
	 */
	ScheduledSlotResource toSlotResource(ScheduledSlotDTO scheduledSlotDTO);

	/**
	 * Maps a list of ScheduledSlotDTOs to a list of ScheduledSlotResources.
	 * @param scheduledSlotDTOs list of ScheduledSlotDTOs
	 * @return mapped list of ScheduledSlotResource
	 */
	List<ScheduledSlotResource> toResourceList(List<ScheduledSlotDTO> scheduledSlotDTOs);

}

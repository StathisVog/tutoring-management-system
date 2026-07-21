package com.education.tutoring.management.application.service.teacher;

import com.education.tutoring.management.application.dto.teacher.TeacherDailyScheduleDTO;
import com.education.tutoring.management.application.port.in.teacher.GetTeacherSchedule;
import com.education.tutoring.management.application.port.out.TeacherPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.application.util.ScheduleDateResolver;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving a teacher's personal daily teaching schedule
 * within a specific date range.
 */
@Slf4j
@UseCase
class GetTeacherScheduleUseCase implements GetTeacherSchedule {

	private final TeacherPersistence teacherPersistence;

	public GetTeacherScheduleUseCase(TeacherPersistence teacherPersistence) {
		this.teacherPersistence = teacherPersistence;
	}

	/**
	 * Executes the retrieval of the teacher's schedule.
	 * @param teacherUsername the username of the requesting teacher
	 * @param startDate the start date of the requested period
	 * @param endDate the end date of the requested period
	 * @return a list of {@link TeacherDailyScheduleDTO} representing the daily scheduled
	 * slots
	 * @throws ResourceNotFoundException if the teacher cannot be found
	 * @throws IllegalOperationException if the provided date range is invalid
	 */
	@Override
	public List<TeacherDailyScheduleDTO> execute(String teacherUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException {

		// Delegates the entire validation and defaulting logic to our central utility
		ScheduleDateResolver.DateRange validRange = ScheduleDateResolver.resolveAndValidate(startDate, endDate);

		log.debug("Executing schedule retrieval for teacher '{}' from {} to {}", teacherUsername,
				validRange.startDate(), validRange.endDate());

		return teacherPersistence.getTeacherSchedule(teacherUsername, validRange.startDate(), validRange.endDate());
	}

}

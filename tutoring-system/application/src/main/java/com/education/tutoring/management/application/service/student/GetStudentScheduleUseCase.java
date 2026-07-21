package com.education.tutoring.management.application.service.student;

import com.education.tutoring.management.application.dto.student.StudentDailyScheduleDTO;
import com.education.tutoring.management.application.port.in.student.GetStudentSchedule;
import com.education.tutoring.management.application.port.out.StudentPersistence;
import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.application.util.ScheduleDateResolver;
import com.education.tutoring.management.domain.exception.IllegalOperationException;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case implementation for retrieving a student's personal daily schedule within a
 * specific date range.
 */
@Slf4j
@UseCase
class GetStudentScheduleUseCase implements GetStudentSchedule {

	private final StudentPersistence studentPersistence;

	public GetStudentScheduleUseCase(StudentPersistence studentPersistence) {
		this.studentPersistence = studentPersistence;
	}

	/**
	 * Executes the retrieval of the student's schedule.
	 * @param studentUsername the username of the requesting student
	 * @param startDate the start date of the requested period
	 * @param endDate the end date of the requested period
	 * @return a list of {@link StudentDailyScheduleDTO} representing the daily schedule
	 * slots
	 * @throws ResourceNotFoundException if the student cannot be found
	 * @throws IllegalOperationException if the provided date range is invalid
	 */
	@Override
	public List<StudentDailyScheduleDTO> execute(String studentUsername, LocalDate startDate, LocalDate endDate)
			throws ResourceNotFoundException, IllegalOperationException {

		// Delegates the entire validation and defaulting logic to our central utility
		ScheduleDateResolver.DateRange validRange = ScheduleDateResolver.resolveAndValidate(startDate, endDate);

		log.debug("Executing schedule retrieval for student '{}' from {} to {}", studentUsername,
				validRange.startDate(), validRange.endDate());

		return studentPersistence.getStudentSchedule(studentUsername, validRange.startDate(), validRange.endDate());
	}

}

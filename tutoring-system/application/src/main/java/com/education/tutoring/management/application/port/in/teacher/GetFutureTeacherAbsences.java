package com.education.tutoring.management.application.port.in.teacher;

import com.education.tutoring.management.application.dto.teacher.TeacherAbsenceDTO;
import com.education.tutoring.management.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for retrieving a teacher's future and present absences.
 */
public interface GetFutureTeacherAbsences {

	/**
	 * Executes the retrieval of a teacher's declared absences. Automatically filters the
	 * results to include only present (today) and future absences, ordered
	 * chronologically. The results encompass both full-day absences and slot-specific
	 * lesson cancellations.
	 * @param teacherUsername the username of the teacher requesting their absence list
	 * @return a chronological list of {@link TeacherAbsenceDTO} objects representing
	 * upcoming absences
	 * @throws ResourceNotFoundException if the specified teacher cannot be found in the
	 * database
	 */
	List<TeacherAbsenceDTO> execute(String teacherUsername) throws ResourceNotFoundException;

}

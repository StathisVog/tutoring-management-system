package com.education.tutoring.management.application.util;

import com.education.tutoring.management.domain.exception.IllegalOperationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Utility class responsible for resolving, validating, and applying rules to schedule
 * date ranges across the application.
 */
public final class ScheduleDateResolver {

	// Preventing class initialization
	private ScheduleDateResolver() {
		throw new AssertionError("Utility class cannot be instantiated");
	}

	/**
	 * A simple record to hold the resolved start and end dates.
	 */
	public record DateRange(LocalDate startDate, LocalDate endDate) {
	}

	/**
	 * Resolves and validates a requested schedule date range. Applies default week
	 * bounds, prevents massive queries, and restricts searches to the current academic
	 * year.
	 * @param startDate the requested start date (can be null)
	 * @param endDate the requested end date (can be null)
	 * @return a valid {@link DateRange}
	 * @throws IllegalOperationException if the range exceeds 31 days or is outside the
	 * academic year
	 */
	public static DateRange resolveAndValidate(LocalDate startDate, LocalDate endDate)
			throws IllegalOperationException {

		// 1. Default Handling: If dates are missing, fetch the current week (Monday to
		// Sunday)
		if (startDate == null || endDate == null) {
			LocalDate today = LocalDate.now();
			startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		}

		// 2. Sanity Check: Swap dates if the user accidentally sends startDate > endDate
		if (startDate.isAfter(endDate)) {
			LocalDate temp = startDate;
			startDate = endDate;
			endDate = temp;
		}

		// 3. Performance Check: Prevent requesting massive date ranges
		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		if (daysBetween > 31) {
			throw new IllegalOperationException("Date range is too large. Maximum allowed range is 31 days.");
		}

		// 4. Business Rule: Restrict to Current Academic Year (September 1st - August
		// 31st)
		LocalDate academicYearStart = getAcademicYearStart(LocalDate.now());
		LocalDate academicYearEnd = academicYearStart.plusYears(1).minusDays(1);

		if (startDate.isBefore(academicYearStart) || endDate.isAfter(academicYearEnd)) {
			throw new IllegalOperationException("Requested dates fall outside the current academic year.");
		}

		return new DateRange(startDate, endDate);
	}

	/**
	 * Helper method to calculate the start date of the current academic year based on a
	 * reference date (usually today). The academic year is defined as starting on
	 * September 1st.
	 * @param referenceDate the date from which to calculate the academic year
	 * @return the LocalDate representing September 1st of the appropriate year
	 */
	private static LocalDate getAcademicYearStart(LocalDate referenceDate) {
		if (referenceDate.getMonthValue() >= Month.SEPTEMBER.getValue()) {
			return LocalDate.of(referenceDate.getYear(), Month.SEPTEMBER, 1);
		}
		else {
			return LocalDate.of(referenceDate.getYear() - 1, Month.SEPTEMBER, 1);
		}
	}

}

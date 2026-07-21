package com.education.tutoring.management.repository.jpa.repository.specification;

import com.education.tutoring.management.repository.jpa.entity.TeacherAbsence;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for generating JPA Specifications related to {@link TeacherAbsence}
 * entities. Provides dynamic query construction with optimized fetch joins, explicitly
 * handling the hybrid nature of absences (both slot-specific and full-day) via safe LEFT
 * JOINs.
 */
public final class TeacherAbsenceSpecification {

	private TeacherAbsenceSpecification() {
	}

	/**
	 * Dynamically builds a SQL WHERE clause for Teacher Absences based on provided
	 * optional filters. Safely eager-fetches the hybrid relationships using LEFT JOIN to
	 * accommodate full-day absences.
	 */
	public static Specification<TeacherAbsence> withDynamicFilters(Long teacherId, Long slotId, LocalDate startDate,
			LocalDate endDate) {

		return (root, query, criteriaBuilder) -> {

			// 1. Eager Fetching to prevent N+1 queries
			if (Long.class != query.getResultType()) {
				root.fetch("teacher", JoinType.INNER);
				// Use LEFT join here because the slot might be null (Full-Day absence)
				Fetch<Object, Object> slotFetch = root.fetch("scheduledSlot", JoinType.LEFT);
				slotFetch.fetch("course", JoinType.LEFT);
			}

			List<Predicate> predicates = new ArrayList<>();

			// 2. Dynamic Conditions
			if (teacherId != null) {
				predicates.add(criteriaBuilder.equal(root.get("teacher").get("id"), teacherId));
			}
			if (slotId != null) {
				predicates.add(criteriaBuilder.equal(root.get("scheduledSlot").get("id"), slotId));
			}

			// 3. Delegate date filtering and sorting to the central Utility
			SpecificationUtils.applyDateFiltersAndSorting(root, query, criteriaBuilder, predicates, startDate, endDate,
					"date");

			// 4. Combine all predicates with an AND operator
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}

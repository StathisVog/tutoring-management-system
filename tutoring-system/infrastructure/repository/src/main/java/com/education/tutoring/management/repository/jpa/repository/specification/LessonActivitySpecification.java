package com.education.tutoring.management.repository.jpa.repository.specification;

import com.education.tutoring.management.repository.jpa.entity.LessonActivity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for generating JPA Specifications related to {@link LessonActivity}
 * entities. Provides dynamic, type-safe query construction for filtering lesson
 * activities based on optional administrative parameters, while optimizing fetch
 * strategies to prevent N+1 issues.
 */
public final class LessonActivitySpecification {

	private LessonActivitySpecification() {
	}

	/**
	 * Dynamically builds a SQL WHERE clause based ONLY on the non-null parameters
	 * provided.
	 */
	public static Specification<LessonActivity> withDynamicFilters(Long teacherId, Long courseId, Long slotId,
			LocalDate startDate, LocalDate endDate) {

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// 1. Prevent N+1 queries by fetching relationships eagerly
			if (Long.class != query.getResultType()) {
				root.fetch("scheduledSlot").fetch("course");
				root.fetch("scheduledSlot").fetch("teacher");
			}

			// 2. Dynamically add conditions ONLY if they are provided
			if (teacherId != null) {
				predicates.add(criteriaBuilder.equal(root.get("scheduledSlot").get("teacher").get("id"), teacherId));
			}
			if (courseId != null) {
				predicates.add(criteriaBuilder.equal(root.get("scheduledSlot").get("course").get("id"), courseId));
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

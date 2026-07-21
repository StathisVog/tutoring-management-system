package com.education.tutoring.management.repository.jpa.repository.specification;

import com.education.tutoring.management.repository.jpa.entity.Test;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for generating JPA Specifications related to {@link Test} entities.
 * Enables dynamic filtering based on administrative criteria while executing deep,
 * distinct fetch joins to efficiently retrieve tests, their authors, courses, and nested
 * student results.
 */
public final class TestSpecification {

	private TestSpecification() {
	}

	/**
	 * Dynamically builds a SQL WHERE clause for Tests, eager-fetching all related data.
	 */
	public static Specification<Test> withDynamicFilters(Long teacherId, Long courseId, LocalDate startDate,
			LocalDate endDate) {

		return (root, query, criteriaBuilder) -> {

			// Ensures we don't return duplicate root entities when using JOIN FETCH on
			// collections
			query.distinct(true);

			// 1. Eager Fetching to prevent N+1 queries
			if (Long.class != query.getResultType()) {
				root.fetch("course", JoinType.INNER);
				root.fetch("author", JoinType.INNER);
				// Fetch the collection of results, and then fetch the student inside each
				// result!
				root.fetch("testResults", JoinType.LEFT).fetch("student", JoinType.LEFT);
			}

			List<Predicate> predicates = new ArrayList<>();

			// 2. Dynamic Conditions
			if (teacherId != null) {
				predicates.add(criteriaBuilder.equal(root.get("author").get("id"), teacherId));
			}
			if (courseId != null) {
				predicates.add(criteriaBuilder.equal(root.get("course").get("id"), courseId));
			}

			// 3. Delegate date filtering and sorting to the central Utility
			SpecificationUtils.applyDateFiltersAndSorting(root, query, criteriaBuilder, predicates, startDate, endDate,
					"date");

			// 4. Combine all predicates with an AND operator
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}

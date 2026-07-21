package com.education.tutoring.management.repository.jpa.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.List;

/**
 * Shared utility class containing reusable Criteria API operations. Designed to
 * centralize common querying patterns, such as date range filtering and sorting,
 * promoting the DRY (Don't Repeat Yourself) principle across all custom JPA
 * Specifications.
 */
public final class SpecificationUtils {

	private SpecificationUtils() {
		// Private constructor to prevent instantiation of utility class
	}

	/**
	 * Appends standard date range filters to the predicate list and applies descending
	 * sorting. Reusable across different specifications to adhere to the DRY principle.
	 * @param root the root entity type
	 * @param query the criteria query
	 * @param criteriaBuilder the criteria builder
	 * @param predicates the current list of predicates to append to
	 * @param startDate the optional lower bound date
	 * @param endDate the optional upper bound date
	 * @param dateFieldName the exact name of the date property in the entity (e.g.,
	 * "date")
	 * @param <T> the entity type
	 */
	public static <T> void applyDateFiltersAndSorting(Root<T> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder, List<Predicate> predicates, LocalDate startDate, LocalDate endDate,
			String dateFieldName) {

		if (startDate != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), startDate));
		}

		if (endDate != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(dateFieldName), endDate));
		}

		// Apply descending sorting based on the date field
		query.orderBy(criteriaBuilder.desc(root.get(dateFieldName)));
	}

}

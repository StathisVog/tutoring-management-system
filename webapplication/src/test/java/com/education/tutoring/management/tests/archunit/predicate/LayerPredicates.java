package com.education.tutoring.management.tests.archunit.predicate;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import org.mapstruct.Mapper;

public class LayerPredicates {

	public static DescribedPredicate<JavaClass> resideInApplicationLayer = new DescribedPredicate<>("pop") {

		@Override
		public boolean test(JavaClass theClass) {
			return theClass.getSimpleName().equals("package-name");
		}
	};

	public static DescribedPredicate<JavaClass> mapper = new DescribedPredicate<>(
			"is an interface or class annotated with @Mapper") {
		@Override
		public boolean test(JavaClass javaClass) {
			return javaClass.isAnnotatedWith(Mapper.class);
		}
	};

}

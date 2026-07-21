package com.education.tutoring.management.tests.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;

import java.util.Set;

public class ContainOnlyOnePublicExecuteMethod extends ArchCondition<JavaClass> {

	private static final int USECASE_PUBLIC_METHODS_LIMIT = 1;

	public ContainOnlyOnePublicExecuteMethod() {
		super("");
	}

	@Override
	public void check(JavaClass clazz, ConditionEvents events) {

		final String name = clazz.getName();
		final Set<JavaMethod> methodsSet = clazz.getMethods();
		int publicMethodsCount = 0;

		for (final JavaMethod javaMethod : methodsSet) {
			if (javaMethod.getModifiers().contains(JavaModifier.PUBLIC)) {
				publicMethodsCount = publicMethodsCount + 1;
			}
		}

		if (publicMethodsCount > USECASE_PUBLIC_METHODS_LIMIT) {
			throw new AssertionError(String.format("Class %s contains %d public methods, when limit is %d", name,
					publicMethodsCount, USECASE_PUBLIC_METHODS_LIMIT));
		}
	}

}

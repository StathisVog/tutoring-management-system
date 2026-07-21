package com.education.tutoring.management.tests.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;

public class InnerClassesInsideInterfaceCondition extends ArchCondition<JavaClass> {

	public InnerClassesInsideInterfaceCondition() {
		super("be declared as an inner class of an interface");
	}

	@Override
	public void check(JavaClass javaClass, ConditionEvents events) {
		JavaClass enclosingClass = javaClass.getEnclosingClass().orElse(null);

		if (enclosingClass == null || !enclosingClass.isInterface()) {
			String message = String.format("The class %s is not declared inside and interface (enclosing class: %s)",
					javaClass.getName(), (enclosingClass != null) ? enclosingClass.getName() : "none");
			throw new AssertionError(message);
		}
	}

}

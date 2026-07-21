package com.education.tutoring.management.tests.archunit.rule.application;

import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PORT_IN_BASE_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PORT_IN_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PORT_OUT_BASE_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PORT_OUT_PACKAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.education.tutoring.management.tests.archunit.condition.ContainOnlyOnePublicExecuteMethod;
import com.education.tutoring.management.tests.archunit.condition.InnerClassesInsideInterfaceCondition;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

public class PortRules {

	@ArchTest
	public static ArchRule input_ports_should_have_only_one_public_execute_method_rule = classes().that()
		.resideInAPackage(APPLICATION_PORT_IN_BASE_PACKAGE)
		.and()
		.areInterfaces()
		.should(new ContainOnlyOnePublicExecuteMethod())
		.allowEmptyShould(true)
		.as("Input ports should have only one public execute method.");

	@ArchTest
	public final ArchRule exceptions_outside_exception_package_in_ports = noClasses().that()
		.resideInAPackage(APPLICATION_PORT_IN_BASE_PACKAGE)
		.and()
		.areAssignableTo(Exception.class)
		.and()
		.areNotNestedClasses()
		.should()
		.resideOutsideOfPackages(APPLICATION_PORT_IN_PACKAGE + ".exception..",
				APPLICATION_PORT_OUT_PACKAGE + ".exception..")
		.allowEmptyShould(true)
		.as("Not nested exception classes should be inside exception package");

	@ArchTest
	private final ArchRule nested_exceptions_outside_ports = noClasses().that()
		.resideInAPackage(APPLICATION_PORT_IN_BASE_PACKAGE)
		.and()
		.areAssignableTo(Exception.class)
		.and()
		.areNestedClasses()
		.should()
		.resideInAnyPackage(APPLICATION_PORT_IN_BASE_PACKAGE, APPLICATION_PORT_OUT_BASE_PACKAGE)
		.andShould(new InnerClassesInsideInterfaceCondition())
		.allowEmptyShould(true)
		.as("Nested exception classes should be inside interfaces");

}

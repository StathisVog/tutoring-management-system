package com.education.tutoring.management.tests.archunit.rule.common;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class NamingConventionRules {

	@ArchTest
	public static final ArchRule mapper_class_naming_rule = classes().that()
		.resideInAPackage("..mapper..")
		.and()
		.areInterfaces()
		.should()
		.haveSimpleNameEndingWith("Mapper")
		.allowEmptyShould(true);

	@ArchTest
	public static final ArchRule exception_class_naming_rule = classes().that()
		.resideInAPackage("..exception..")
		.and()
		.areAssignableTo(Throwable.class)
		.should()
		.haveSimpleNameEndingWith("Exception")
		.allowEmptyShould(true);

}

package com.education.tutoring.management.tests.archunit.rule.application;

import com.education.tutoring.management.application.service.annotation.UseCase;
import com.education.tutoring.management.tests.archunit.condition.ContainOnlyOnePublicExecuteMethod;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_SERVICE_BASE_PACKAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class UseCaseRules {

	@ArchTest
	public static final ArchRule usecase_should_not_be_public_rule = classes().that()
		.resideInAPackage(APPLICATION_SERVICE_BASE_PACKAGE)
		.and()
		.areAnnotatedWith(UseCase.class)
		.should()
		.notBePublic()
		.allowEmptyShould(true)
		.as("Usecase classes should not be public")
		.because("They should only be used through the input ports");

	@ArchTest
	public static final ArchRule usecase_suffix_rule = classes().that()
		.resideInAPackage(APPLICATION_SERVICE_BASE_PACKAGE)
		.and()
		.areAnnotatedWith(UseCase.class)
		.should()
		.haveSimpleNameEndingWith("UseCase")
		.allowEmptyShould(true)
		.as("Usecase classes should have 'UseCase' suffix");

	@ArchTest
	public static final ArchRule usecase_should_have_only_one_public_method_rule = classes().that()
		.areAnnotatedWith(UseCase.class)
		.should(new ContainOnlyOnePublicExecuteMethod())
		.allowEmptyShould(true)
		.because("Use Cases should have only one business responsibility.");

	@ArchTest
	public static final ArchRule usecase_should_implement_input_port_rule = classes().that()
		.resideInAPackage(APPLICATION_SERVICE_BASE_PACKAGE)
		.and()
		.areAnnotatedWith(UseCase.class)
		.should()
		.implement(JavaClass.Predicates.resideInAPackage("..port.in.."))
		.allowEmptyShould(true)
		.as("Usecase should implement an input port");

	@ArchTest
	public static final ArchRule usecase_final_fields_rule = classes().that()
		.resideInAPackage(APPLICATION_SERVICE_BASE_PACKAGE)
		.and()
		.areAnnotatedWith(UseCase.class)
		.should()
		.haveOnlyFinalFields()
		.allowEmptyShould(true)
		.as("Usecase classes should have final fields");

	@ArchTest
	public static final ArchRule usecase_public_method_name_rule = methods().that()
		.arePublic()
		.and()
		.areDeclaredInClassesThat()
		.areAnnotatedWith(UseCase.class)
		.should()
		.haveName("execute")
		.allowEmptyShould(true)
		.as("Usecase should only have a public method called execute");

}

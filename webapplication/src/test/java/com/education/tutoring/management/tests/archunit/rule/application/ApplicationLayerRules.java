package com.education.tutoring.management.tests.archunit.rule.application;

import com.education.tutoring.management.TutoringManagementSystemApplication;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_LAYER_BASE_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_PORT_PACKAGE;
import static com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerProperties.APPLICATION_SERVICE_PACKAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.education.tutoring.management.tests.archunit.predicate.LayerPredicates.mapper;

public class ApplicationLayerRules {

	@ArchTest
	public static final ArchRule application_service_package_naming_convention = classes().that()
		.haveSimpleName("package-info")
		.and()
		.areAnnotatedWith(org.jmolecules.architecture.hexagonal.Application.class)
		.should()
		.resideInAPackage(APPLICATION_SERVICE_PACKAGE);

	@ArchTest
	public static final ArchRule input_port_package_naming_convention = classes().that()
		.haveSimpleName("package-info")
		.and()
		.areAnnotatedWith(PrimaryPort.class)
		.should()
		.resideInAPackage(APPLICATION_PORT_PACKAGE + ".in");

	@ArchTest
	public static final ArchRule output_port_package_naming_convention = classes().that()
		.haveSimpleName("package-info")
		.and()
		.areAnnotatedWith(SecondaryPort.class)
		.should()
		.resideInAPackage(APPLICATION_PORT_PACKAGE + ".out");

	@ArchTest
	public static final ArchRule application_classes_should_not_be_outside_service_or_port_packages_rule = noClasses()
		.should()
		.resideInAPackage(APPLICATION_PACKAGE)
		.andShould()
		.resideOutsideOfPackages(APPLICATION_SERVICE_PACKAGE, APPLICATION_PORT_PACKAGE)
		.as("Application classes should be on service or port packages");

	@ArchTest
	public static final ArchRule application_classes_should_not_use_runtime_exceptions_rule = noClasses().that()
		.resideInAPackage(APPLICATION_LAYER_BASE_PACKAGE)
		.and()
		.areNotAssignableTo(mapper)
		.should()
		.dependOnClassesThat()
		.areAssignableTo(RuntimeException.class)
		.as("Runtime exceptions are not allowed in the Application layer except in generated classes");

	@ArchTest
	public static final ArchRule application_classes_should_not_import_forbidden_dependencies_rule = noClasses().that()
		.resideInAPackage(APPLICATION_LAYER_BASE_PACKAGE)
		.should()
		.dependOnClassesThat()
		.resideOutsideOfPackages(TutoringManagementSystemApplication.class.getPackageName() + "..", "lombok..",
				"java..", "org.jmolecules..", "org.mapstruct..", "edu.umd.cs.findbugs.annotations..", "org.slf4j..",
				"jakarta.transaction")
		.as("Application classes should be on service or port packages");

}

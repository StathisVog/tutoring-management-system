package com.education.tutoring.management.tests.archunit;

import com.education.tutoring.management.tests.archunit.rule.application.ApplicationLayerRules;
import com.education.tutoring.management.tests.archunit.rule.application.PortRules;
import com.education.tutoring.management.tests.archunit.rule.application.UseCaseRules;
import com.education.tutoring.management.tests.archunit.rule.common.CodingRules;
import com.education.tutoring.management.tests.archunit.rule.common.NamingConventionRules;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.lang.ArchRule;

import static org.jmolecules.archunit.JMoleculesArchitectureRules.ensureHexagonal;

@AnalyzeClasses(packages = "com.education.tutoring.management",
		importOptions = { ImportOption.DoNotIncludeTests.class })
public class ArchUnitTest {

	@ArchTest
	private final ArchRule ensure_hexagonal_architecture = ensureHexagonal();

	@ArchTest
	public static final ArchTests coding_rules = ArchTests.in(CodingRules.class);

	@ArchTest
	public static final ArchTests naming_convention_rules = ArchTests.in(NamingConventionRules.class);

	@ArchTest
	public static final ArchTests application_layer_rules = ArchTests.in(ApplicationLayerRules.class);

	@ArchTest
	public static final ArchTests port_rules = ArchTests.in(PortRules.class);

	@ArchTest
	public static final ArchTests usecase_rules = ArchTests.in(UseCaseRules.class);

}

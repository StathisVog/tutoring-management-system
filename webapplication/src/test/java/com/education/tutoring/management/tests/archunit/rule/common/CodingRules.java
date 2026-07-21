package com.education.tutoring.management.tests.archunit.rule.common;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;

import java.util.logging.Logger;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

public class CodingRules {

	@ArchTest
	private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

	@ArchTest
	private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

	@ArchTest
	private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

	@ArchTest
	private final ArchRule loggers_should_be_private_static_final = fields().that()
		.haveRawType(Logger.class)
		.should()
		.bePrivate()
		.andShould()
		.beStatic()
		.andShould()
		.beFinal()
		.because("we agreed on this convention")
		.allowEmptyShould(true);

	@ArchTest
	private final ArchRule no_jodatime = NO_CLASSES_SHOULD_USE_JODATIME;

	@ArchTest
	private final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

	@ArchTest
	static final ArchRule no_classes_should_access_standard_streams_or_throw_generic_exceptions = CompositeArchRule
		.of(NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
		.and(NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS);

	@ArchTest
	static final ArchRule no_accesses_to_upper_package = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

}

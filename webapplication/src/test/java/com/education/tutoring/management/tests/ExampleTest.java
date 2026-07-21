package com.education.tutoring.management.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleTest {

	@Test
	void testExample() {
		// Act
		String text = getText();

		// Assert
		String expected = "This is a test to avoid problems with Jenkins and Sonar";

		assertEquals(expected, text, "failure - should be true");
	}

	private String getText() {
		return "This is a test to avoid problems with Jenkins and Sonar";
	}

}

package com.education.tutoring.management.domain.util;

import com.education.tutoring.management.domain.exception.InvalidSchoolClassException;

/**
 * Enumerates the available educational grade levels (Junior High and High School),
 * mapping internal representations to human-readable descriptive values.
 */
public enum SchoolClass {

	A_GUMNASIOU("Junior High School - 1st Year"), B_GUMNASIOU("Junior High School - 2nd Year"),
	C_GUMNASIOU("Junior High School - 3rd Year"),

	A_LUKEIOU("High School - 1st Year"), B_LUKEIOU("High School - 2nd Year"), C_LUKEIOU("High School - 3rd Year");

	private final String value;

	SchoolClass(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	/**
	 * Resolves a SchoolClass from its string representation.
	 * @param text the string value of the class
	 * @return the matching {@link SchoolClass} enum
	 * @throws InvalidSchoolClassException if the text is null, blank, or does not match
	 * any class
	 */
	public static SchoolClass fromValue(String text) {
		if (text == null || text.isBlank()) {
			throw new InvalidSchoolClassException("School class value cannot be null or empty.");
		}

		for (SchoolClass schoolClass : SchoolClass.values()) {
			// Accepts both 'A_LUKEIOU' & 'High School - 1st Year' in a SchoolClass field
			if (schoolClass.value().equalsIgnoreCase(text.trim()) || schoolClass.name().equalsIgnoreCase(text.trim())) {
				return schoolClass;
			}
		}

		throw new InvalidSchoolClassException("Invalid school class provided: '" + text + "'");
	}

}

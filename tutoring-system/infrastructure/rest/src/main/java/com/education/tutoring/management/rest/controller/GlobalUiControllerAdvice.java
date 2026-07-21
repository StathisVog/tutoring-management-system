package com.education.tutoring.management.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global UI Configuration. Intercepts all requests returning views and adds common UI
 * attributes.
 */
@ControllerAdvice
public class GlobalUiControllerAdvice {

	/**
	 * Injects the current Request URI into every Thymeleaf model globally. Accessible in
	 * HTML via ${currentUri}
	 */
	@ModelAttribute("currentUri")
	public String getCurrentUri(HttpServletRequest request) {
		return request.getRequestURI();
	}

}

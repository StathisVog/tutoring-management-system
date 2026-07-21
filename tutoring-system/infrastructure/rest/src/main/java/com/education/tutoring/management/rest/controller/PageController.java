package com.education.tutoring.management.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI Controller responsible for serving HTML templates via Thymeleaf. Maps frontend URL
 * routes to their corresponding view templates.
 */
@Controller
public class PageController {

	// --- PUBLIC ROUTES ---

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/courses")
	public String courses() {
		return "guest-courses";
	}

	// --- ADMIN UI ROUTES ---
	@GetMapping("/admin/users")
	public String adminUsersPage() {
		return "admin-users";
	}

	@GetMapping("/admin/courses")
	public String adminCoursesPage() {
		return "admin-courses";
	}

	@GetMapping("/admin/schedule")
	public String adminSchedulePage() {
		return "admin-schedule";
	}

	@GetMapping("/admin/enrollments")
	public String adminEnrollmentsPage() {
		return "admin-enrollments";
	}

	@GetMapping("/admin/audit")
	public String adminAuditPage() {
		return "admin-audit";
	}

	// --- TEACHER UI ROUTES ---
	@GetMapping("/teacher/schedule")
	public String teacherSchedulePage() {
		return "teacher-schedule";
	}

	@GetMapping("/teacher/tests")
	public String teacherTestsPage() {
		return "teacher-tests";
	}

	@GetMapping("/teacher/absences")
	public String teacherAbsencesPage() {
		return "teacher-absences";
	}

	@GetMapping("/teacher/profile")
	public String teacherProfilePage(Model model) {
		model.addAttribute("userRole", "TEACHER");
		return "user-profile";
	}

	// --- STUDENT UI ROUTES ---

	@GetMapping("/student/schedule")
	public String studentSchedulePage() {
		return "student-schedule";
	}

	@GetMapping("/student/enrollments")
	public String studentEnrollmentsPage() {
		return "student-enrollments";
	}

	@GetMapping("/student/tests")
	public String studentTestsPage() {
		return "student-tests";
	}

	@GetMapping("/student/profile")
	public String studentProfilePage(Model model) {
		model.addAttribute("userRole", "STUDENT");
		return "user-profile";
	}

}

package com.education.tutoring.management.rest.exception;

import com.education.tutoring.management.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler that intercepts application exceptions and translates them
 * into standardized HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles out custom Domain exception.
	 * @param ex the thrown {@link RegisterUserException} containing the validation
	 * message
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request status
	 */
	@ExceptionHandler(RegisterUserException.class)
	public ResponseEntity<ErrorResponse> handleRegisterUserException(RegisterUserException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown during the login process, such as invalid credentials or
	 * attempts to log in with an inactive account.
	 * @param ex the thrown {@link LoginUserException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 401 Unauthorized status
	 */
	@ExceptionHandler(LoginUserException.class)
	public ResponseEntity<ErrorResponse> handleLoginUserException(LoginUserException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
				ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	/**
	 * Handles database-level constraint violations, such as schema-level validations
	 * triggered during entity persistence.
	 * @param ex the thrown {@link ConstraintViolationException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request status
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {

		String errorMessage = ex.getConstraintViolations()
			.stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.joining(" | "));

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Database Constraint Violation",
				errorMessage, LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions when a requested resource is not found.
	 * @param ex the thrown {@link ResourceNotFoundException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 404 Not Found status
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when an invalid school class is provided.
	 * @param ex the thrown {@link InvalidSchoolClassException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request response
	 */
	@ExceptionHandler(InvalidSchoolClassException.class)
	public ResponseEntity<ErrorResponse> handleInvalidSchoolClassException(InvalidSchoolClassException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when a business rule is violated or an illegal operation
	 * is attempted.
	 * @param ex the thrown {@link IllegalOperationException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request response
	 */
	@ExceptionHandler(IllegalOperationException.class)
	public ResponseEntity<ErrorResponse> handleIllegalOperationException(IllegalOperationException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when an attempt is made to create a course that already
	 * exists.
	 * @param ex the thrown {@link CourseAlreadyExistsException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 409 Conflict response
	 */
	@ExceptionHandler(CourseAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleCourseAlreadyExistsException(CourseAlreadyExistsException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Course Conflict", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when a teacher is not assigned to the specified course.
	 * @param ex the thrown {@link TeacherNotAssignedToCourseException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request response
	 */
	@ExceptionHandler(TeacherNotAssignedToCourseException.class)
	public ResponseEntity<ErrorResponse> handleTeacherNotAssignedToCourseException(
			TeacherNotAssignedToCourseException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when a student attempts to enroll in a scheduled slot
	 * that has already reached its maximum capacity.
	 * @param ex the thrown {@link CapacityExceededException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 409 Conflict status
	 */
	@ExceptionHandler(CapacityExceededException.class)
	public ResponseEntity<ErrorResponse> handleCapacityExceededException(CapacityExceededException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when a student attempts to enroll in a scheduled slot
	 * they are already actively enrolled in.
	 * @param ex the thrown {@link AlreadyEnrolledException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request status
	 */
	@ExceptionHandler(AlreadyEnrolledException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyEnrolledException(AlreadyEnrolledException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when a user tries to access or modify a resource without
	 * the proper ownership or permissions.
	 * @param ex the thrown {@link UnauthorizedActionException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 403 Forbidden status
	 */
	@ExceptionHandler(UnauthorizedActionException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedActionException(UnauthorizedActionException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	/**
	 * Handles exceptions thrown when an enrollment is in an inappropriate state for the
	 * requested operation.
	 * @param ex the thrown {@link InvalidEnrollmentStateException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request status
	 */
	@ExceptionHandler(InvalidEnrollmentStateException.class)
	public ResponseEntity<ErrorResponse> handleInvalidEnrollmentStateException(InvalidEnrollmentStateException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
				LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles validation exceptions thrown during the profile update process.
	 * @param ex the thrown {@link UpdateUserException}
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 400 Bad Request status
	 */
	@ExceptionHandler(UpdateUserException.class)
	public ResponseEntity<ErrorResponse> handleUpdateUserException(UpdateUserException ex) {

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Profile Update Error",
				ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Handles 404 Not Found errors triggered by incorrect URLs. Distinguishes between
	 * REST API calls (returns JSON) and UI requests (forwards to error.html).
	 * @param ex the thrown {@link NoResourceFoundException}
	 * @param request the incoming HTTP request to check the URL path
	 * @return a JSON Response for API calls, or throws the exception back to Spring for
	 * UI routing
	 * @throws Exception re-throws the exception if it's a UI request to trigger
	 * BasicErrorController
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex,
			HttpServletRequest request) throws Exception {

		String requestUri = request.getRequestURI();

		if (requestUri.startsWith("/api/")) {

			ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Endpoint Not Found",
					"The requested API endpoint does not exist!", LocalDateTime.now());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}

		throw ex;
	}

	/**
	 * Catch-all fallback handler for any unexpected exceptions not explicitly caught by
	 * other handlers. Prevents raw stack traces from reaching the client.
	 * @return a {@link ResponseEntity} containing the standardized {@link ErrorResponse}
	 * with a 500 Internal Server Error status
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

		log.error("Unhandled exception caught in GlobalExceptionHandler: ", ex);

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error", "An unexpected error occurred. Please try again later.", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	/**
	 * Handles validation errors thrown by @Valid annotations on request payloads.
	 * Extracts both field-specific and global object errors to construct a detailed
	 * response.
	 * @param ex the exception thrown when validation on an argument annotated with @Valid
	 * fails
	 * @return a {@link ResponseEntity} containing the structured {@link ErrorResponse}
	 * with HTTP 400 status
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		// Join all default messages into a single readable string
		String combinedErrors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining(" | "));

		// Fallback in case there are only global errors
		if (combinedErrors.isEmpty()) {
			combinedErrors = "Invalid input data provided.";
		}

		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error", combinedErrors,
				LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

}

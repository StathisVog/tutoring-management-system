package com.education.tutoring.management.rest.model.user;

import com.education.tutoring.management.domain.util.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Polymorphic REST API response representing a core user profile, dynamically serialized
 * into specific subtypes (Student or Teacher).
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "role",
		visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = StudentResource.class, name = "STUDENT"),
		@JsonSubTypes.Type(value = TeacherResource.class, name = "TEACHER") })
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Base user profile information, extended by specific user roles")
public class UserResource {

	@Schema(description = "The unique identifier of the user", example = "1")
	private Long id;

	@Schema(description = "The username of the user", example = "exampleuser")
	private String username;

	@Schema(description = "The contact email address of the user", example = "exampleuser@example.com")
	private String email;

	@Schema(description = "The full name of the user", example = "example")
	private String fullName;

	@Schema(description = "The home address of the user", example = "Athens, Greece")
	private String address;

	@Schema(description = "The preferred UI theme color hex code for the user", example = "theme-default")
	private String themeColor;

	@Schema(description = "The filename or identifier of the user's avatar image", example = "avatar-default.svg")
	private String avatarName;

	@Schema(description = "The system role of the user, used for polymorphic deserialization", example = "STUDENT")
	private Role role;

	@Schema(description = "Indicates whether the account has been approved and activated", example = "true")
	private boolean enabled;

	@Schema(description = "The timestamp when the account was created", example = "2026-05-15T10:30:00")
	private LocalDateTime createdAt;

}

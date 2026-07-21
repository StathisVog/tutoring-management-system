package com.education.tutoring.management.application.dto.user;

import com.education.tutoring.management.domain.util.Role;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the base user account details.
 */
@Data
public class UserDTO {

	private Long id;

	private String username;

	private String email;

	private String fullName;

	private String address;

	private String themeColor;

	private String avatarName;

	private Role role;

	private boolean enabled;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String passwordHash;

}

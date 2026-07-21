package com.education.tutoring.management.repository.jpa.entity;

import com.education.tutoring.management.domain.util.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base JPA Entity representing an authenticated user in the system. Utilizes JOINED
 * inheritance to serve as the root for specific user types (e.g., Student, Teacher),
 * while centralizing common credentials, profile data, and auditing timestamps.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "passwordHash", "securityAnswerHash" })
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;

	@Size(min = 3, max = 50)
	@Column(name = "username", nullable = false, unique = true, length = 50)
	private String username;

	@Email
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@Column(name = "address")
	private String address;

	@Builder.Default
	@Column(name = "theme_color", length = 30)
	private String themeColor = "theme-default";

	@Builder.Default
	@Column(name = "avatar_name", length = 50)
	private String avatarName = "avatar-default.svg";

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private Role role;

	@Builder.Default
	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@Column(name = "security_question")
	private String securityQuestion;

	@Column(name = "security_answer_hash")
	private String securityAnswerHash;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}

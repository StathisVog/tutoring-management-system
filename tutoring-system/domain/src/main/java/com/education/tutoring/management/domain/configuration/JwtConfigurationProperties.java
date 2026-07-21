package com.education.tutoring.management.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties class for JSON Web Token (JWT) settings. Automatically binds
 * properties prefixed with "spring.jwt" from the application's configuration environment.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfigurationProperties {

	private String secret;

	private long expiration;

}

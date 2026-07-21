package com.education.tutoring.management;

import com.education.tutoring.management.application.service.annotation.UseCase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * The main class of a Spring-based application, used for starting it.
 */
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@ComponentScan(includeFilters = { @ComponentScan.Filter(value = { UseCase.class }) })
public class TutoringManagementSystemApplication {

	/**
	 * Main method starting the Spring application.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TutoringManagementSystemApplication.class, args);
	}

}

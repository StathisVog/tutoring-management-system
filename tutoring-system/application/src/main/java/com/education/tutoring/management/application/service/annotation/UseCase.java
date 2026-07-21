package com.education.tutoring.management.application.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom {@code Annotation} used to "label" (or annotate) the classes that implement
 * functionality as application {@code services}.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCase {

	String value() default "";

}

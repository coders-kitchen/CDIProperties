package com.coderskitchen.cdiproperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the injection point of a property
 *
 * The property is loaded from the file specified by @PropertyFile annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Property {
	/**
	 * The name of the property
	 *
	 * @return the properties name
	 */
	String value();
}

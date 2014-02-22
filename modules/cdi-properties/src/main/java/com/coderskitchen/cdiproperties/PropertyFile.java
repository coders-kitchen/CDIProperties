package com.coderskitchen.cdiproperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation gives a hint where the properties file is located
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PropertyFile {
	/**
	 * Name and path of the properties file
	 *
	 * @return the value
	 */
	String value();
}

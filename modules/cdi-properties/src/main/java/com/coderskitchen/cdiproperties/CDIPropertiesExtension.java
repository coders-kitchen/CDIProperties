/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Peter Daum
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.coderskitchen.cdiproperties;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This extension enables CDI to inject properties from a property file.
 * <p/>
 * For achieving this goal, this extension makes CDI aware of @PropertyFile and @Property annotation.
 * <p/>
 * The value of the annotation @PropertyFile points to the property file that contains the properties required
 * by @Property annotations.
 * <p/>
 * <b>Example</b>
 * Given the properties file example.properties with the content
 * <pre>
 *   author=peter
 *   country=germany
 * </pre>
 * <p/>
 * These properties can now injected as shown in the following class Author
 * <pre>
 *
 * {@literal @}PropertyFile("example.properties")
 * public class Author {
 *   {@literal @}Property("author");
 *   private String author;
 *   {@literal @}Property("country")
 *   private String country;
 *
 *   public String getAuthor() {
 *     return author;
 *   }
 *
 *   public String getCountry() {
 *     return country;
 *   }
 * }
 * </pre>
 */
public class CDIPropertiesExtension implements Extension {

	public static final String MESSAGE_PROPERTIES_FROM = "Properties from %s";
	public static final String MESSAGE_PROPERTY_KEY_VALUE = "%s = %s";
	Logger logger = Logger.getLogger(CDIPropertiesExtension.class.getName());

	/**
	 * Specifies the base properties folder for the lookup in the file system
	 */
	public static final String PROPERTY_BASE_FOLDER = "com.coderskitchen.cdiproperties.baseFolder";
	/**
	 * Specifies that the lookup should first attempt to load the file from the file system
	 * <p/>
	 * By default the classpath is preferred
	 */
	public static final String PROPERTY_PREFER_FILE_SYSTEM = "com.coderskitchen.cdiproperties.preferFileSystem";

	/**
	 * Specifies that the lookup should use a internal cache for properties files
	 * <p/>
	 * By default the cache is not used
	 */
	public static final String PROPERTY_USE_CACHING = "com.coderskitchen.cdiproperties.useCaching";
	/**
	 * Exception message when the properties couldn't found
	 */
	public static final String PROPERTIES_FILE_NOT_FOUND = "Properties file [%s] not found!";

	private static final boolean PREFER_FILE_SYSTEM = Boolean.valueOf(System.getProperty(PROPERTY_PREFER_FILE_SYSTEM, "false"));
	private static final boolean USE_CACHING = Boolean.valueOf(System.getProperty(PROPERTY_USE_CACHING, "false"));
	private static final String PROPERTIES_BASE_FOLDER = System.getProperty(PROPERTY_BASE_FOLDER, "");

	private static final Map<String, Properties> PROPERTIES_CACHE = new HashMap<String, Properties>();

	/**
	 * Prepares the injection process of properties from a property file.
	 *
	 * @param pit
	 * 		The actual target of process injection
	 * @param <T>
	 * 		the generic type of the injection target
	 */
	public <T> void initializePropertyLoading(@Observes final ProcessInjectionTarget<T> pit) throws IOException {
		AnnotatedType<T> at = pit.getAnnotatedType();
		if (!at.isAnnotationPresent(PropertyFile.class)) {
			return;
		}
		try {
			PropertyFile propertyFile = at.getAnnotation(PropertyFile.class);
			Properties properties = loadProperties(propertyFile, pit.getAnnotatedType().getJavaClass());
			Map<Field, Object> fieldValues = assignPropertiesToFields(at.getFields(), properties);
			InjectionTarget<T> wrapped = new PropertyInjectionTarget<T>(fieldValues, pit, pit.getInjectionTarget());
			pit.setInjectionTarget(wrapped);
		} catch (Exception e) {
			pit.addDefinitionError(e);
		}
	}

	private Properties loadProperties(PropertyFile propertyFile, Class fromClass) throws IOException {
		String filename = propertyFile.value();
		Properties properties;
		if (USE_CACHING && PROPERTIES_CACHE.containsKey(filename)) {
			properties = PROPERTIES_CACHE.get(filename);
		} else {
			properties = loadPropertiesFromFile(filename, fromClass);
			PROPERTIES_CACHE.put(filename, properties);
		}

		logger.log(Level.FINER, String.format(MESSAGE_PROPERTIES_FROM, filename));
		for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
			logger.log(Level.FINER, String.format(MESSAGE_PROPERTY_KEY_VALUE, objectObjectEntry.getKey(), objectObjectEntry.getValue()));
		}

		return properties;
	}

	private Properties loadPropertiesFromFile(String filename, Class fromClass) throws IOException {
		Properties properties = new Properties();
		InputStream propertiesStream;
		if (PREFER_FILE_SYSTEM) {
			propertiesStream = loadPropertiesFromResources(filename, fromClass);
			if (propertiesStream == null) {
				propertiesStream = fromClass.getClassLoader().getResourceAsStream(filename);
			}
		} else {
			propertiesStream = loadPropertiesFromResources(filename, fromClass);
			if (propertiesStream == null) {
				propertiesStream = loadPropertyFromFileSystem(filename);
			}
		}
		if (propertiesStream == null) {
			throw new IllegalArgumentException(String.format(PROPERTIES_FILE_NOT_FOUND, filename));
		}
		properties.load(propertiesStream);
		return properties;
	}

	private InputStream loadPropertiesFromResources(String filename, Class fromClass) {
		InputStream propertiesStream = fromClass.getClassLoader().getResourceAsStream(filename);
		if (propertiesStream == null) {
			propertiesStream = fromClass.getResourceAsStream(filename);
		}
		return propertiesStream;
	}

	private InputStream loadPropertyFromFileSystem(String filename) throws IOException {
		Path path = Paths.get(PROPERTIES_BASE_FOLDER, filename);
		if (Files.exists(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}

	private <T> Map<Field, Object> assignPropertiesToFields(Set<AnnotatedField<? super T>> fields, Properties properties) {
		Map<Field, Object> fieldValues = new HashMap<Field, Object>();
		for (AnnotatedField<? super T> field : fields) {
			if (field.isAnnotationPresent(Property.class)) {
				Property property = field.getAnnotation(Property.class);
				Object value = properties.get(property.value());
				Field memberField = field.getJavaMember();
				fieldValues.put(memberField, value);
			}
		}
		return fieldValues;
	}
}

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

/**
 * This extension enables CDI to inject properties from a property file.
 *
 * For achieving this goal, this extension makes CDI aware of @PropertyFile and @Property annotation.
 *
 * The value of the annotation @PropertyFile points to the property file that contains the properties required
 * by @Property annotations.
 *
 * <b>Example</b>
 * Given the properties file example.properties with the content
 * <pre>
 *   author=peter
 *   country=germany
 * </pre>
 *
 * These properties can now injected as shown in the following class Author
 * <pre>
 *
 * @PropertyFile("example.properties") public class Author {
 * @Property("author"); private String author;
 * @Property("country") private String country;
 *
 * public String getAuthor() {
 * return author;
 * }
 *
 * public String getCountry() {
 * return country;
 * }
 * }
 * </pre>
 */
public class CDIPropertiesExtension implements Extension {

	/**
	 * Specifies the base properties folder for the lookup in the file system
	 */
	public static final String PROPERTY_BASE_FOLDER = "com.coderskitchen.cdiproperties.baseFolder";
	/**
	 * Specifies that the lookup should first attempt to load the file from the file system
	 *
	 * By default the classpath is preferred
	 */
	public static final String PROPERTY_PREFER_FILE_SYSTEM = "com.coderskitchen.cdiproperties.preferFileSystem";
	/**
	 * Exception message when the properties couldn't found
	 */
	public static final String PROPERTIES_FILE_NOT_FOUND = "Properties file [%s] not found!";

	private static final boolean PREFER_FILE_SYSTEM = Boolean.valueOf(System.getProperty(PROPERTY_PREFER_FILE_SYSTEM, "false"));
	private static final String PROPERTIES_BASE_FOLDER = System.getProperty(PROPERTY_BASE_FOLDER, "");

	private final Map<Field, Object> fieldValues = new HashMap<Field, Object>();

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
		PropertyFile propertyFile = at.getAnnotation(PropertyFile.class);
		String filename = propertyFile.value();
		Properties properties = loadProperties(filename);
		assignPropertiesToFields(at.getFields(), properties);

		InjectionTarget<T> wrapped = new PropertyInjectionTarget<T>(fieldValues, pit, pit.getInjectionTarget());
		pit.setInjectionTarget(wrapped);
	}

	private Properties loadProperties(String filename) throws IOException {
		Properties properties = new Properties();
		InputStream propertiesStream;
		if (PREFER_FILE_SYSTEM) {
			propertiesStream = loadPropertyFromFileSystem(filename);
			if (propertiesStream == null) {
				propertiesStream = getClass().getClassLoader().getResourceAsStream(filename);
			}
		} else {
			propertiesStream = getClass().getClassLoader().getResourceAsStream(filename);
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

	private InputStream loadPropertyFromFileSystem(String filename) throws IOException {
		Path path = Paths.get(PROPERTIES_BASE_FOLDER, filename);
		if (Files.exists(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}

	private <T> void assignPropertiesToFields(Set<AnnotatedField<? super T>> fields, Properties properties) {
		for (AnnotatedField<? super T> field : fields) {
			if (field.isAnnotationPresent(Property.class)) {
				Property property = field.getAnnotation(Property.class);
				Object value = properties.get(property.value());
				Field memberField = field.getJavaMember();
				fieldValues.put(memberField, value);
			}
		}
	}
}

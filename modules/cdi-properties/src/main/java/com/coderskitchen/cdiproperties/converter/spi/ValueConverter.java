package com.coderskitchen.cdiproperties.converter.spi;

/**
 * Generic converter.
 *
 * @param <T>
 * 		generic type that determines to which type a value can be converted
 */
public interface ValueConverter<T> {
	/**
	 * Returns true, if this convert can convert values of type class
	 *
	 * @param valueClass the type of the target value
	 * @return true, if this converter can convert the value to the requested type, false otherwise
	 */
	boolean accept(Class<T> valueClass);

	/**
	 * Convert value to value of type T
	 *
	 * @param value the to be converted value
	 * @return the converted value
	 */
	T convert(Object value);
}

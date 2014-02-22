package com.coderskitchen.cdiproperties;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

import java.util.ServiceLoader;

/**
 * Factory for making the lookup of value converter
 *
 * Created by peter on 2/21/14.
 */
public class ValueConverterFactory {
	private static final ServiceLoader<ValueConverter> CONVERTERS  = ServiceLoader.load(ValueConverter.class);

	/**
	 * Utility class needs a private constructor
	 */
	private ValueConverterFactory() {
	}

	/**
	 * Finds a accepting converter for the given type.
	 * @param type the lookup type
	 * @return the accepting converter or null
	 */
	public static ValueConverter findConverterForFieldType(Class<?> type) {
		ValueConverter acceptingConverter = null;

		if(type == null) {
			return null;
		}

		for (ValueConverter converter : CONVERTERS) {
			if (converter.accept(type)) {
				acceptingConverter = converter;
				break;
			}
		}
		return acceptingConverter;
	}
}

package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Converts value objects to their String representation
 *
 * Created by peter on 2/4/14.
 */
public class StringConverter implements ValueConverter<String> {
	@Override
	public boolean accept(Class<String> valueClass) {
		return valueClass == String.class;
	}

	@Override
	public String convert(Object value) {
		return value.toString();
	}
}

package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Converts a given value into a float representation
 */
public class FloatConverter implements ValueConverter<Float> {
	@Override
	public boolean accept(Class<Float> valueClass) {
		return valueClass.isAssignableFrom(float.class) || valueClass == Float.class;
	}

	@Override
	public Float convert(Object value) {
		return Float.valueOf(value.toString());
	}
}

package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Converts to boolean
 *
 * Created by peter on 2/11/14.
 */
public class BooleanConverter implements ValueConverter<Boolean> {
	@Override
	public boolean accept(Class<Boolean> valueClass) {
		return valueClass.isAssignableFrom(boolean.class) || valueClass == Boolean.class;
	}

	@Override
	public Boolean convert(Object value) {
		return Boolean.valueOf(value.toString());
	}
}

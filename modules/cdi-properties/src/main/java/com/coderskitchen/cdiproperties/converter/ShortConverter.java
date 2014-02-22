package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Converts to short
 *
 * Created by peter on 2/11/14.
 */
public class ShortConverter implements ValueConverter<Short> {
	@Override
	public boolean accept(Class<Short> valueClass) {
		return valueClass.isAssignableFrom(short.class) || valueClass == Short.class;
	}

	@Override
	public Short convert(Object value) {
		return Short.valueOf(value.toString());
	}
}

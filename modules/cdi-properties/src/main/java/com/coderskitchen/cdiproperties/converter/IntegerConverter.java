package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * converts value objects to their Integer representation
 *
 * Created by peter on 2/4/14.
 */
public class IntegerConverter implements ValueConverter<Integer> {
	@Override
	public boolean accept(Class<Integer> valueClass) {
			return valueClass.isAssignableFrom(int.class) || valueClass == Integer.class;
	}

	@Override
	public Integer convert(Object value) {
		return Integer.valueOf(value.toString());
	}
}

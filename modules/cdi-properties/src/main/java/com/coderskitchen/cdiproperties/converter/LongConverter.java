package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * converts value objects to their Integer representation
 *
 * Created by peter on 2/4/14.
 */
public class LongConverter implements ValueConverter<Long> {
	@Override
	public boolean accept(Class<Long> valueClass) {
			return valueClass.isAssignableFrom(long.class) || valueClass == Long.class;
	}

	@Override
	public Long convert(Object value) {
		return Long.valueOf(value.toString());
	}
}

package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Converts to byte
 *
 * Created by peter on 2/11/14.
 */
public class ByteConverter implements ValueConverter<Byte> {
	@Override
	public boolean accept(Class<Byte> valueClass) {
		return valueClass.isAssignableFrom(byte.class) || valueClass == Byte.class;
	}

	@Override
	public Byte convert(Object value) {
		return Byte.valueOf(value.toString());
	}
}

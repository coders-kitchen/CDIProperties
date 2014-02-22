package com.coderskitchen.cdiproperties.converter;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

/**
 * Created by peter on 2/11/14.
 */
public class DoubleConverter implements ValueConverter<Double> {
	@Override
	public boolean accept(Class<Double> valueClass) {
		return valueClass.isAssignableFrom(double.class) || valueClass == Double.class;
	}

	@Override
	public Double convert(Object value) {
		return Double.valueOf(value.toString());
	}
}

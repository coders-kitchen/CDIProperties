/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Peter Daum
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

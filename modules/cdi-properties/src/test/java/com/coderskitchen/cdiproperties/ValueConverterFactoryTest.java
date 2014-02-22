package com.coderskitchen.cdiproperties;

import com.coderskitchen.cdiproperties.converter.BooleanConverter;
import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.List;

public class ValueConverterFactoryTest {

	@Test
	public void lookupIsNullSafe() throws Exception {
		ValueConverter converter = ValueConverterFactory.findConverterForFieldType(null);
		assertThat(converter, nullValue());
	}

	@Test
	public void missingTypeReturnsNull() throws Exception {
		ValueConverter converter = ValueConverterFactory.findConverterForFieldType(List.class);
		assertThat(converter, nullValue());
	}

	@Test
	public void availableConverterReturnsConverter() throws Exception {
		ValueConverter converterForFieldType = ValueConverterFactory.findConverterForFieldType(Boolean.class);
		assertThat(converterForFieldType, instanceOf(BooleanConverter.class));
	}
}

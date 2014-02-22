package com.coderskitchen.cdiproperties;

import com.coderskitchen.cdiproperties.converter.spi.ValueConverter;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Injection target implementation that takes care of injecting properties read from a property file
 *
 * Created by peter on 2/4/14.
 */
public class PropertyInjectionTarget<T> implements InjectionTarget<T> {

	public static final String MESSAGE_NO_CONVERTER_FOUND = "For field %s of type %s in class %s no value converter was found";
	public static final String MESSAGE_NO_VALUE_FOUND = "For field %s of type %s in class %s no value or default was defined";
	private Map<Field, Object> injectableValues = new HashMap<Field, Object>();
	private final ProcessInjectionTarget<T> pit;
	private final InjectionTarget<T> it;

	/**
	 * Constructor accepting all required values for injection
	 *
	 * @param injectableValues
	 * 		- injectable values for this instances of this type
	 * @param pit
	 * 		- the process injection target
	 * @param it
	 * 		- The current instance to be injected
	 */
	public PropertyInjectionTarget(final Map<Field, Object> injectableValues, final ProcessInjectionTarget<T> pit, InjectionTarget<T> it) {
		this.injectableValues = injectableValues;
		this.pit = pit;
		this.it = it;

	}

	@Override
	public void inject(T instance, CreationalContext<T> ctx) {
		it.inject(instance, ctx);
		for (Field field : injectableValues.keySet()) {
			Object value = injectableValues.get(field);
			Class<?> type = field.getType();
			ValueConverter acceptingConverter = ValueConverterFactory.findConverterForFieldType(type);
			setFieldValueOrAddDefinitionError(instance, field, value, acceptingConverter);
		}
	}


	private void setFieldValueOrAddDefinitionError(T instance, Field field, Object value, ValueConverter acceptingConverter) {
		if (acceptingConverter == null) {
			pit.addDefinitionError(new InjectionException(String.format(MESSAGE_NO_CONVERTER_FOUND, field.getName(), field.getType(), pit.getAnnotatedType().getJavaClass().getName())));
		} else if (value == null) {
			pit.addDefinitionError(new InjectionException(String.format(MESSAGE_NO_VALUE_FOUND, field.getName(), field.getType(), pit.getAnnotatedType().getJavaClass().getName())));
		} else {
			try {
				Object convert = acceptingConverter.convert(value);
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				field.set(instance, convert);
				field.setAccessible(accessible);
			} catch (IllegalAccessException e) {
				pit.addDefinitionError(e);
			}
		}
	}

	@Override
	public void postConstruct(T instance) {
		it.postConstruct(instance);
	}

	@Override
	public void preDestroy(T instance) {
		it.preDestroy(instance);
	}

	@Override
	public T produce(CreationalContext<T> ctx) {
		return it.produce(ctx);
	}

	@Override
	public void dispose(T instance) {
		it.dispose(instance);
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return it.getInjectionPoints();
	}
}

/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

/**
 * {@link ConfigurablePropertyAccessor} implementation that directly accesses
 * instance fields. Allows for direct binding to fields instead of going through
 * JavaBean setters.
 *
 * <p>As of Spring 4.2, the vast majority of the {@link BeanWrapper} features have
 * been merged to {@link AbstractPropertyAccessor}, which means that property
 * traversal as well as collections and map access is now supported here as well.
 *
 * <p>A DirectFieldAccessor's default for the "extractOldValueForEditor" setting
 * is "true", since a field can always be read without side effects.
 *
 * <p>
 *     ConfigurablePropertyAccessor实现，直接访问实例字段。允许直接绑定到字段，而不是通过JavaBean setter。
 * <br>
 * 从Spring 4.2开始，绝大多数BeanWrapper功能已合并到AbstractPropertyAccessor，这意味着现在也支持属性遍历以及集合和映射访问。
 * DirectFieldAccessor的“extractOldValueForEditor”设置的默认值为“true”，因为始终可以在没有副作用的情况下读取字段。
 * </p>
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 2.0
 * @see #setExtractOldValueForEditor
 * @see BeanWrapper
 * @see org.springframework.validation.DirectFieldBindingResult
 * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
 */
public class DirectFieldAccessor extends AbstractNestablePropertyAccessor {

	private final Map<String, FieldPropertyHandler> fieldMap = new HashMap<>();


	/**
	 * Create a new DirectFieldAccessor for the given object.
	 * @param object object wrapped by this DirectFieldAccessor
	 */
	public DirectFieldAccessor(Object object) {
		super(object);
	}

	/**
	 * Create a new DirectFieldAccessor for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this DirectFieldAccessor
	 * @param nestedPath the nested path of the object
	 * @param parent the containing DirectFieldAccessor (must not be {@code null})
	 */
	protected DirectFieldAccessor(Object object, String nestedPath, DirectFieldAccessor parent) {
		super(object, nestedPath, parent);
	}


	@Override
	protected FieldPropertyHandler getLocalPropertyHandler(String propertyName) {
		FieldPropertyHandler propertyHandler = this.fieldMap.get(propertyName);
		if (propertyHandler == null) {
			Field field = ReflectionUtils.findField(getWrappedClass(), propertyName);
			if (field != null) {
				propertyHandler = new FieldPropertyHandler(field);
			}
			this.fieldMap.put(propertyName, propertyHandler);
		}
		return propertyHandler;
	}

	@Override
	protected DirectFieldAccessor newNestedPropertyAccessor(Object object, String nestedPath) {
		return new DirectFieldAccessor(object, nestedPath, this);
	}

	@Override
	protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
		PropertyMatches matches = PropertyMatches.forField(propertyName, getRootClass());
		throw new NotWritablePropertyException(
				getRootClass(), getNestedPath() + propertyName,
				matches.buildErrorMessage(), matches.getPossibleMatches());
	}


	private class FieldPropertyHandler extends PropertyHandler {

		private final Field field;

		public FieldPropertyHandler(Field field) {
			super(field.getType(), true, true);
			this.field = field;
		}

		@Override
		public TypeDescriptor toTypeDescriptor() {
			return new TypeDescriptor(this.field);
		}

		@Override
		public ResolvableType getResolvableType() {
			return ResolvableType.forField(this.field);
		}

		@Override
		public TypeDescriptor nested(int level) {
			return TypeDescriptor.nested(this.field, level);
		}

		@Override
		public Object getValue() throws Exception {
			try {
				ReflectionUtils.makeAccessible(this.field);
				return this.field.get(getWrappedInstance());
			}

			catch (IllegalAccessException ex) {
				throw new InvalidPropertyException(getWrappedClass(),
						this.field.getName(), "Field is not accessible", ex);
			}
		}

		@Override
		public void setValue(Object object, Object value) throws Exception {
			try {
				ReflectionUtils.makeAccessible(this.field);
				this.field.set(object, value);
			}
			catch (IllegalAccessException ex) {
				throw new InvalidPropertyException(getWrappedClass(), this.field.getName(),
						"Field is not accessible", ex);
			}
		}
	}

}
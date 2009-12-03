package org.openmrs.module.serialization.xstream.mapper;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * This mapper and CustomReflectionConverter together implement the function to let xstream ignore those unknown elements while deserialize from a string
 * 
 * @see org.openmrs.module.serialization.xstream.converter.CustomReflectionConverter
 */
public class IgnoreUnknownElementMapper extends MapperWrapper {

	public IgnoreUnknownElementMapper(Mapper wrapped) {
		super(wrapped);
	}

	/**
	 * When xstream deserialize a unknow element, it will throw "CannotResolveClassException", <br/>
	 * here just don't do anything for this exception, so that xstream can ignore those unknown elements.
	 */
	public Class realClass(String elementName) {
		Class clazz = null;
		try {
			clazz = super.realClass(elementName);
		} catch (CannotResolveClassException e) {
		} finally {
			return clazz;
		}
	}
}

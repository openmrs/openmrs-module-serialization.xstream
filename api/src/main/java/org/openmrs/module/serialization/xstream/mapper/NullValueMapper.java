package org.openmrs.module.serialization.xstream.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Ensures that Null Values (particularly in Maps and Collections do not bomb the system
 */
public class NullValueMapper extends MapperWrapper {

	public NullValueMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	/**
	 * @see Mapper#serializedClass(java.lang.Class)
	 */
	public String serializedClass(Class type) {
		if (type == null) {
			return "null";
		}
		return super.serializedClass(type);
	}
}
